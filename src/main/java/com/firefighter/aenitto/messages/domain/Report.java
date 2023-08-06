package com.firefighter.aenitto.messages.domain;

import com.firefighter.aenitto.common.baseEntities.CreationLog;
import com.firefighter.aenitto.members.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends CreationLog {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "report_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reporter_id")
  private Member reporter;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "message_id")
  private Message message;

  @ColumnDefault(value = "false")
  private boolean resolved;

  public boolean isResolved() {
    return resolved;
  }

  public void resolveReport() {
    resolved = true;
  }
}
