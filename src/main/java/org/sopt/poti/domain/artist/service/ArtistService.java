package org.sopt.poti.domain.artist.service;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.artist.dto.response.ArtistListResponse;
import org.sopt.poti.domain.artist.entity.Artist;
import org.sopt.poti.domain.artist.repository.ArtistRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;

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
}
