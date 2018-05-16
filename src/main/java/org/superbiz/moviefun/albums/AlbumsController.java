package org.superbiz.moviefun.albums;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private final TransactionOperations albumsOperations;

    public AlbumsController(AlbumsBean albumsBean, @Qualifier("transactionOperationsForAlbums") TransactionOperations albumsOperations) {
        this.albumsBean = albumsBean;
        this.albumsOperations  = albumsOperations;
    }


    @GetMapping("/albums")
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }
}
