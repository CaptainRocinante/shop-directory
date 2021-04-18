package com.rocinante.datastore.elastic;

import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;

public class ProductsElasticsearchAnalysisConfigurer implements ElasticsearchAnalysisConfigurer {
  @Override
  public void configure(ElasticsearchAnalysisConfigurationContext context) {
    context
        .analyzer("custom_english")
        .custom()
        .tokenizer("standard")
        .tokenFilters("lowercase", "snowball_english", "asciifolding");

    context.tokenFilter("snowball_english").type("snowball").param("language", "English");

    context
        .analyzer("name")
        .custom()
        .tokenizer("standard")
        .tokenFilters("lowercase", "asciifolding");
  }
}
