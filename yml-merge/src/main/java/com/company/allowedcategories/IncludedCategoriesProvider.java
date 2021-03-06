package com.company.allowedcategories;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by user50 on 11.07.2015.
 */
public class IncludedCategoriesProvider {

    private AllCategoriesProvider allCategoriesProvider;
    private Set<String> categoryIds;

    public IncludedCategoriesProvider(AllCategoriesProvider allCategoriesProvider, Set<String> categoryIds) {
        this.allCategoriesProvider = allCategoriesProvider;
        this.categoryIds = categoryIds;
    }

    public Set<String> get() throws FileNotFoundException, XMLStreamException {
        Set<Category> allCategories = allCategoriesProvider.get();

        Set<Category> categoriesFromConfig = allCategories.stream()
                .filter(category -> categoryIds.isEmpty() || categoryIds.contains(category.getId()))
                .collect(Collectors.toSet());

        ListMultimap<String, Category> multimap = ArrayListMultimap.create();

        for (Category category : allCategories) {
            multimap.get(category.getParentId()).add(category);
        }

        Set<Category> categoriesToInclude = new Util(multimap).getDescendants(categoriesFromConfig);

        return categoriesToInclude.stream().map(Category::getId).collect(Collectors.toSet());
    }
}
