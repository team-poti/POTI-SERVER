package org.sopt.poti.domain.banner.repository;

import java.util.List;
import org.sopt.poti.domain.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {

  List<Banner> findByActiveTrueOrderBySortOrderAsc();
}
