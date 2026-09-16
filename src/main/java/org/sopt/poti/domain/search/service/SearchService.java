package org.sopt.poti.domain.search.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.artist.service.ArtistService;
import org.sopt.poti.domain.feed.dto.response.FeedGroupItem;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.search.dto.response.SearchSuggestionResponse;
import org.sopt.poti.domain.search.dto.response.SearchSuggestionResponse.SuggestionItem;
import org.sopt.poti.domain.search.dto.response.SearchSuggestionResponse.SuggestionType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

  private static final int SUGGESTION_LIMIT = 5;

  private final GroupBuyRepository groupBuyRepository;
  private final ArtistService artistService;

  public SearchSuggestionResponse getSuggestions(String keyword) {
    if (keyword == null || keyword.trim().isBlank()) {
      return SearchSuggestionResponse.of(List.of());
    }

    List<SuggestionItem> suggestions = new ArrayList<>();

    // 아티스트명 자동완성
    artistService.searchArtists(keyword.trim()).artists().stream()
        .map(a -> new SuggestionItem(SuggestionType.ARTIST, a.name()))
        .forEach(suggestions::add);

    // 분철글 제목 자동완성
    groupBuyRepository.findTitlesByNgramGlobal(keyword.trim(), SUGGESTION_LIMIT).stream()
        .map(title -> new SuggestionItem(SuggestionType.TITLE, title))
        .forEach(suggestions::add);

    return SearchSuggestionResponse.of(suggestions);
  }

  public Slice<FeedGroupItem> search(String keyword, Pageable pageable) {
    if (keyword == null || keyword.trim().isBlank()) {
      return new SliceImpl<>(List.of(), pageable, false);
    }
    return groupBuyRepository.searchByKeyword(keyword.trim(), pageable);
  }
}
