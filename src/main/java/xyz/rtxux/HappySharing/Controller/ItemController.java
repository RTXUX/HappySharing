package xyz.rtxux.HappySharing.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import xyz.rtxux.HappySharing.Exception.AppException;
import xyz.rtxux.HappySharing.Exception.BadRequestException;
import xyz.rtxux.HappySharing.Exception.ResourceNotFoundException;
import xyz.rtxux.HappySharing.Model.ImageInfo;
import xyz.rtxux.HappySharing.Model.Item;
import xyz.rtxux.HappySharing.Model.User;
import xyz.rtxux.HappySharing.Payload.ApiResponse;
import xyz.rtxux.HappySharing.Payload.ItemJson;
import xyz.rtxux.HappySharing.Repository.ItemRepository;
import xyz.rtxux.HappySharing.Repository.UserRepository;
import xyz.rtxux.HappySharing.Security.UserPrincipal;
import xyz.rtxux.HappySharing.Service.ItemSearchService;
import xyz.rtxux.HappySharing.Service.UserMessageService;
import xyz.rtxux.HappySharing.Util.Mapper.MyMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/item")
public class ItemController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemSearchService itemSearchService;

    @Autowired
    private UserMessageService userMessageService;


    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "", params = {})
    public List<ItemJson> getItems(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return itemRepository.findAllByOwner(userRepository.findById(userPrincipal.getId()).get()).stream().map(item ->
            //new ItemJson(item.getId(), item.getOwner().getId(), item.getName(),item.getDescription(), item.getLocation(),item.getPrice(), item.getImages().stream().map(ImageInfo::getId).collect(Collectors.toList()), item.getDuration(), item.getStatus())
                MyMapper.itemJsonFromItem(item)
                        .images(item.getImages().stream().map(ImageInfo::getId).collect(Collectors.toList()))
                        .build()
        ).collect(Collectors.toList());
    }

    @GetMapping(value = "", params = {"search"})
    public List<ItemJson> searchItems(@RequestParam String search) {
        List<Item> items = itemSearchService.searchItem(search);

        if (items!=null) {
            return items.stream().map(item -> {
                return ItemJson.builder()
                        .id(item.getId())
                        .description(item.getDescription())
                        .duration(item.getDuration())
                        .images(item.getImages().stream().map(ImageInfo::getId).collect(Collectors.toList()))
                        .location(item.getLocation())
                        .name(item.getName())
                        .price(item.getPrice())
                        .status(item.getStatus())
                        .build();
            }).collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    //@PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ItemJson getItem(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        var itemOpt = itemRepository.findById(id);
        if (!itemOpt.isPresent()) throw new ResourceNotFoundException("Item", "ID", id.toString());
        var item = itemOpt.get();
        var itemJsonBuilder = MyMapper.itemJsonFromItem(item).images(item.getImages().stream().map(ImageInfo::getId).collect(Collectors.toList()));
        if (userPrincipal != null && userPrincipal.getId().equals(item.getOwner().getId())) {
            itemJsonBuilder = itemJsonBuilder.borrower_id(item.getBorrower()!=null?item.getBorrower().getId():null);
        }

        return itemJsonBuilder.build();
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<?> postItem(@RequestBody ItemJson itemJson, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        var userOpt = userRepository.findById(userPrincipal.getId());
        if (!userOpt.isPresent()) return ResponseEntity.badRequest().build();
        var user = userOpt.get();
        var item = new Item();
        item.setDescription(itemJson.getDescription());
        item.setLocation(itemJson.getLocation());
        item.setName(itemJson.getName());
        item.setPrice(itemJson.getPrice());
        item.setOwner(user);
        item.setStatus(0);
        item.setDuration(itemJson.getDuration());
        item = itemRepository.save(item);

        itemJson.setId(item.getId());
        //itemJson.setImages(item.getImages().stream().map(ImageInfo::getId).collect(Collectors.toList()));
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentContextPath().path("/item/{id}").buildAndExpand(item.getId()).toUri()).body(itemJson);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<?> deleteItem(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        var itemOpt = itemRepository.findById(id);
        if (!itemOpt.isPresent()) return ResponseEntity.notFound().build();
        var item = itemOpt.get();
        if (item.getOwner().getId()!=userPrincipal.getId()) {
            throw new AccessDeniedException("Not your resource");
        }
        if (item.isBorrowerLocked()) {
            throw new BadRequestException("Locked item");
        }
        itemRepository.delete(item);
        return ResponseEntity.ok(new ApiResponse(0,null));
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<?> updateItem(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody ItemJson itemJson) {


        var itemOpt = itemRepository.findById(id);
        if (!itemOpt.isPresent()) return ResponseEntity.notFound().build();
        var item = itemOpt.get();
        if (item.getOwner().getId()!=userPrincipal.getId()) {
            throw new AccessDeniedException("Not your resource");
        }
        if (item.isPublished()||item.isBorrowerLocked()||item.isAccepted()||item.isReturned()) {
            return ResponseEntity.badRequest().body(new ApiResponse(1, Map.of("message", "Illegal operation")));
        }
        item.setPrice(itemJson.getPrice());
        item.setStatus(itemJson.getStatus());
        item.setName(itemJson.getName());
        item.setDescription(itemJson.getDescription());
        item.setLocation(itemJson.getLocation());
        item.setDuration(itemJson.getDuration());
        item = itemRepository.save(item);
        return ResponseEntity.ok(itemJson);
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<?> publishItem(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long id) {
        var user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new AppException("You seems to be authorized but user not found"));
        var item = itemRepository.findById(id).orElseThrow(() -> new BadRequestException("item not found"));
        if (!user.getId().equals(item.getOwner().getId())) throw new BadRequestException("Not your item");
        item.setPublished(true);
        item = itemRepository.save(item);
        userMessageService.sendMessageFromSystemTo(user.getId(), String.format("您发布了物品：%s",item.getName()));
        return ResponseEntity.ok(new ApiResponse(0, Map.of("message", "Item successfully published")));
    }



    @PreAuthorize("hasRole('USER')")
    @GetMapping(params = {"borrowed"})
    public ResponseEntity<?> getBorrowedItem(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        var userOpt = userRepository.findById(userPrincipal.getId());
        User user = userOpt.orElseThrow();
        List<Item> borrowedItems = itemRepository.findAllByBorrower(user);
        var borrowedItemJsons = borrowedItems.stream().map(item -> MyMapper.itemJsonFromItem(item)
                .images(item.getImages().stream().map(ImageInfo::getId).collect(Collectors.toList()))
                .build()
        ).collect(Collectors.toList());
        return ResponseEntity.ok(borrowedItemJsons);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/borrow")
    @Transactional
    public ResponseEntity<?> borrowLockItem(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long id) {
        var user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new AppException("You seems to be authorized but user not found"));
        var item = itemRepository.findById(id).orElseThrow(() -> new BadRequestException("Item not found"));
        if (item.isReturned()||item.isBorrowerLocked()||!item.isPublished()||item.getOwner().getId().equals(user.getId())) {
            throw new BadRequestException("Illegal Operation");
        }
        item.setBorrower(user);
        item.setBorrowerLocked(true);
        item = itemRepository.save(item);
        userMessageService.sendMessageFromSystemTo(user.getId(), String.format("您已请求借用物品：%s，等待对方回复",item.getName()));
        userMessageService.sendMessageFromSystemTo(item.getOwner().getId(), String.format("您的物品 %s 被用户 %s 请求借用",item.getName(),user.getUsername()));
        return ResponseEntity.ok(new ApiResponse(0, Map.of("message", "Successfully locked item")));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/accept")
    @Transactional
    public ResponseEntity<?> acceptBorrower(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long id, @RequestParam boolean reject) {
        var user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new AppException("You seems to be authorized but user not found"));
        var item = itemRepository.findById(id).orElseThrow(() -> new BadRequestException("Item not found"));
        if (!item.getOwner().getId().equals(user.getId())) throw new BadRequestException("Not your item");
        if (!item.isBorrowerLocked()||item.isReturned()) {
            throw new BadRequestException("Illegal Operation");
        }
        if (reject) {
            item.setBorrowerLocked(false);
            userMessageService.sendMessageFromSystemTo(item.getBorrower().getId(), String.format("您借用物品 %s 的请求被对方拒绝", item.getName()));
            item.setBorrower(null);
        } else {
            item.setAccepted(true);
            userMessageService.sendMessageFromSystemTo(user.getId(), String.format("您已同意 %s 借用您 %s 物品的请求", item.getBorrower().getUsername(), item.getName()));
            userMessageService.sendMessageFromSystemTo(item.getBorrower().getId(), String.format("您借用 %s 物品的请求已被同意", item.getName()));
        }
        item = itemRepository.save(item);
        return ResponseEntity.ok(new ApiResponse(0, Map.of("message", "Success")));


    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/return")
    @Transactional
    public ResponseEntity<?> returnItem(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long id) {
        var user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new AppException("You seems to be authorized but user not found"));
        var item = itemRepository.findById(id).orElseThrow(() -> new BadRequestException("Item not found"));
        if (!item.getOwner().getId().equals(userPrincipal.getId())) throw new AccessDeniedException("Not your item");
        if (!item.isAccepted()||item.isReturned()) throw new BadRequestException("Illegal Operation");
        item.setReturned(true);
        item = itemRepository.save(item);
        userMessageService.sendMessageFromSystemTo(user.getId(), String.format("您已确认您的物品 %s 已归还", item.getName()));
        userMessageService.sendMessageFromSystemTo(item.getBorrower().getId(), String.format("您借用的物品 %s 已被确认归还", item.getName()));
        return ResponseEntity.ok(new ApiResponse(0, Map.of("message", "Success")));
    }





}
