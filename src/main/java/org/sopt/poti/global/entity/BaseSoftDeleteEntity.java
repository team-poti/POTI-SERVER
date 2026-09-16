package org.sopt.poti.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseSoftDeleteEntity extends BaseTimeEntity {

  @Column(name = "deleted_at")
  protected LocalDateTime deletedAt;  // 자식이 써야하므로 protected
}
