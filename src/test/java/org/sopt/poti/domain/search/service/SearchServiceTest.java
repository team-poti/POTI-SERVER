package org.sopt.poti.domain.search.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.poti.domain.artist.dto.response.ArtistTitlesResponse;
import org.sopt.poti.domain.artist.dto.response.ArtistTitlesResponse.ArtistTitleDto;
import org.sopt.poti.domain.artist.service.ArtistService;
import org.sopt.poti.domain.feed.dto.response.FeedGroupItem;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.search.dto.response.SearchSuggestionResponse;
import org.sopt.poti.domain.search.dto.response.SearchSuggestionResponse.SuggestionType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@DisplayName("SearchService")
class SearchServiceTest {

  private GroupBuyRepository groupBuyRepository;
  private ArtistService artistService;
  private SearchService searchService;

  @BeforeEach
  void setUp() {
    groupBuyRepository = mock(GroupBuyRepository.class);
    artistService = mock(ArtistService.class);
    searchService = new SearchService(groupBuyRepository, artistService);
  }

  @Nested
  @DisplayName("자동완성 - getSuggestions")
  class GetSuggestions {

    @Test
    @DisplayName("빈 키워드 → 빈 리스트 반환, 레포지토리 호출 없음")
    void emptyKeyword() {
      SearchSuggestionResponse result = searchService.getSuggestions("  ");

      assertThat(result.suggestions()).isEmpty();
      verify(artistService, never()).searchArtists(anyString());
      verify(groupBuyRepository, never()).findTitlesByNgramGlobal(anyString(), anyInt());
    }

    @Test
    @DisplayName("null 키워드 → 빈 리스트 반환")
    void nullKeyword() {
      SearchSuggestionResponse result = searchService.getSuggestions(null);

      assertThat(result.suggestions()).isEmpty();
    }

    @Test
    @DisplayName("아티스트 결과가 제목 결과보다 앞에 위치")
    void artistBeforeTitle() {
      when(artistService.searchArtists("뉴진스")).thenReturn(
          ArtistTitlesResponse.of(List.of(new ArtistTitleDto(1L, "뉴진스")))
      );
      when(groupBuyRepository.findTitlesByNgramGlobal(eq("뉴진스"), anyInt())).thenReturn(
          List.of("뉴진스 버블검 포카")
      );

      List<SearchSuggestionResponse.SuggestionItem> suggestions =
          searchService.getSuggestions("뉴진스").suggestions();

      assertThat(suggestions).hasSize(2);
      assertThat(suggestions.get(0).type()).isEqualTo(SuggestionType.ARTIST);
      assertThat(suggestions.get(0).value()).isEqualTo("뉴진스");
      assertThat(suggestions.get(1).type()).isEqualTo(SuggestionType.TITLE);
      assertThat(suggestions.get(1).value()).isEqualTo("뉴진스 버블검 포카");
    }

    @Test
    @DisplayName("아티스트 결과 없고 제목만 있는 경우")
    void onlyTitleResults() {
      when(artistService.searchArtists("버블검")).thenReturn(
          ArtistTitlesResponse.of(List.of())
      );
      when(groupBuyRepository.findTitlesByNgramGlobal(eq("버블검"), anyInt())).thenReturn(
          List.of("뉴진스 버블검 포카", "버블검 앨범")
      );

      List<SearchSuggestionResponse.SuggestionItem> suggestions =
          searchService.getSuggestions("버블검").suggestions();

      assertThat(suggestions).hasSize(2);
      assertThat(suggestions).allMatch(s -> s.type() == SuggestionType.TITLE);
    }

    @Test
    @DisplayName("아티스트와 제목 모두 결과 없는 경우 → 빈 리스트")
    void noResults() {
      when(artistService.searchArtists("없는검색어")).thenReturn(
          ArtistTitlesResponse.of(List.of())
      );
      when(groupBuyRepository.findTitlesByNgramGlobal(eq("없는검색어"), anyInt())).thenReturn(
          List.of()
      );

      assertThat(searchService.getSuggestions("없는검색어").suggestions()).isEmpty();
    }
  }

  @Nested
  @DisplayName("검색 결과 - search")
  class Search {

    @Test
    @DisplayName("빈 키워드 → 빈 Slice 반환, 레포지토리 호출 없음")
    void emptyKeyword() {
      Slice<FeedGroupItem> result = searchService.search("", PageRequest.of(0, 20));

      assertThat(result.getContent()).isEmpty();
      assertThat(result.hasNext()).isFalse();
      verify(groupBuyRepository, never()).searchByKeyword(anyString(), any());
    }

    @Test
    @DisplayName("정상 키워드 → 레포지토리 결과 그대로 반환")
    void normalKeyword() {
      FeedGroupItem item = new FeedGroupItem("뉴진스", 1L, "image.jpg", "버블검 포카", 3L, "인기");
      Slice<FeedGroupItem> fakeSlice = new SliceImpl<>(List.of(item), PageRequest.of(0, 20), false);

      when(groupBuyRepository.searchByKeyword("뉴진스", PageRequest.of(0, 20))).thenReturn(fakeSlice);

      Slice<FeedGroupItem> result = searchService.search("뉴진스", PageRequest.of(0, 20));

      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().get(0).artist()).isEqualTo("뉴진스");
    }

    @Test
    @DisplayName("키워드 앞뒤 공백 제거 후 검색")
    void trimKeyword() {
      when(groupBuyRepository.searchByKeyword("뉴진스", PageRequest.of(0, 20)))
          .thenReturn(new SliceImpl<>(List.of(), PageRequest.of(0, 20), false));

      searchService.search("  뉴진스  ", PageRequest.of(0, 20));

      verify(groupBuyRepository).searchByKeyword("뉴진스", PageRequest.of(0, 20));
    }
  }
}
