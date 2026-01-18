package org.sopt.poti.domain.artist.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.artist.dto.response.ArtistListResponse;
import org.sopt.poti.domain.artist.dto.response.ArtistTitlesResponse;
import org.sopt.poti.domain.artist.dto.response.MemberListResponse;
import org.sopt.poti.domain.artist.dto.response.MemberListResponse.MemberResponse;
import org.sopt.poti.domain.artist.entity.Artist;
import org.sopt.poti.domain.artist.repository.ArtistRepository;
import org.sopt.poti.domain.artist.repository.MemberRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

  private final ArtistRepository artistRepository;
  private final MemberRepository memberRepository;

  public Artist getById(Long artistId) {
    return artistRepository.findById(artistId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.ARTIST_NOT_FOUND));
  }

  public ArtistListResponse getArtists() {
    List<Artist> artists = artistRepository.findAll();

    return new ArtistListResponse(
        artists.stream()
            .map(a -> new ArtistListResponse.ArtistItemResponse(
                a.getId(),
                a.getName(),
                a.getLogoImageUrl()
            ))
            .toList()
    );
  }

  public MemberListResponse getMembers(Long artistId) {
    Artist artist = artistRepository.findById(artistId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.ARTIST_NOT_FOUND));

    List<MemberResponse> list = memberRepository.findByArtist(artist)
        .stream()
        .map(member -> new MemberResponse(member.getId(), member.getName()))
        .toList();

    return new MemberListResponse(list);
  }

  public ArtistTitlesResponse searchArtists(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return ArtistTitlesResponse.of(List.of());
    }

    List<ArtistTitlesResponse.ArtistTitleDto> artists =
        artistRepository.findByPrefix(keyword.trim(), 5);
    return ArtistTitlesResponse.of(artists);
  }

}
