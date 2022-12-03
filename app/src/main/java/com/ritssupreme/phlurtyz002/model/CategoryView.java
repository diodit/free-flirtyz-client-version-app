package com.ritssupreme.phlurtyz002.model;

import java.util.List;

/**
 * Created by kibrom on 5/29/17.
 */

public class CategoryView {

    private String categoryName;

    private List<CategoryAsset> categoryAssetList;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<CategoryAsset> getCategoryAssetList() {
        return categoryAssetList;
    }

    public void setCategoryAssetList(List<CategoryAsset> categoryAssetList) {

        this.categoryAssetList = categoryAssetList;
    }
}
