package org.sopt.poti.domain.search.dto.response;

import java.util.List;

public record SearchSuggestionResponse(List<SuggestionItem> suggestions) {

  public enum SuggestionType {
    ARTIST, TITLE
  }

  public record SuggestionItem(SuggestionType type, String value) {}

  public static SearchSuggestionResponse of(List<SuggestionItem> suggestions) {
    return new SearchSuggestionResponse(suggestions);
  }
}
