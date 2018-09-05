package xyz.rtxux.HappySharing.Service;


import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.rtxux.HappySharing.Model.Item;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class ItemSearchService {
    @PersistenceContext
    private EntityManager centityManager;



    @Transactional
    public void initializeSearchService() {
        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public List<Item> searchItem(String searchTerm) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Item.class).get();
        Query searchQuery = queryBuilder.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("name", "description").matching(searchTerm).createQuery();
        javax.persistence.Query query = fullTextEntityManager.createFullTextQuery(searchQuery,Item.class);


        List<Item> items = null;
        try {
            items = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
