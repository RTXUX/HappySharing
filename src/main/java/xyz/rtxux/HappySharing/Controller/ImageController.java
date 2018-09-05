package xyz.rtxux.HappySharing.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;
import xyz.rtxux.HappySharing.Model.ImageDatabaseStorage;
import xyz.rtxux.HappySharing.Model.ImageInfo;
import xyz.rtxux.HappySharing.Model.ImageStorageType;
import xyz.rtxux.HappySharing.Payload.ApiResponse;
import xyz.rtxux.HappySharing.Repository.ImageDatabaseStorageRepository;
import xyz.rtxux.HappySharing.Repository.ImageInfoRepository;
import xyz.rtxux.HappySharing.Repository.ItemRepository;
import xyz.rtxux.HappySharing.Repository.UserRepository;
import xyz.rtxux.HappySharing.Security.UserPrincipal;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private ImageInfoRepository imageInfoRepository;

    @Autowired
    private ImageDatabaseStorageRepository imageDatabaseStorageRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<?> getImage(@PathVariable Long id) {
        var imgInfoOpt = imageInfoRepository.findById(id);
        if (!imgInfoOpt.isPresent()) return ResponseEntity.notFound().build();
        var imgInfo = imgInfoOpt.get();
        switch (imgInfo.getImageStorageType()) {
            case IMAGE_STORAGE_DATABASE: {
                var imgOpt = imageDatabaseStorageRepository.findById(Long.parseLong(imgInfo.getStorageIdentity()));
                if (!imgOpt.isPresent()) return ResponseEntity.noContent().build();
                var img = imgOpt.get();
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(img.getData());
            }
            default: {
                return ResponseEntity.noContent().build();
            }
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<?> uploadImage(@RequestParam("item") Long itemId, @RequestParam("file") MultipartFile image, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        var user = userRepository.findById(userPrincipal.getId()).orElseThrow();
        var item = itemRepository.findById(itemId).orElseThrow();
        if (!item.getOwner().getId().equals(user.getId())) throw new AccessDeniedException("Not your resource");
        ImageDatabaseStorage img = new ImageDatabaseStorage();
        try {
            img.setData(image.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerErrorException("");
        }
        img = imageDatabaseStorageRepository.save(img);
        var imgInfo = new ImageInfo();
        imgInfo.setImageStorageType(ImageStorageType.IMAGE_STORAGE_DATABASE);
        imgInfo.setItem(item);
        imgInfo.setOwner(user);
        imgInfo.setStorageIdentity(img.getId().toString());
        imgInfo = imageInfoRepository.save(imgInfo);
        return ResponseEntity.ok(new ApiResponse(0,Map.of("image_id",imgInfo.getId())));
    }


}
