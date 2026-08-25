package com.realkoreatravel.localscore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.realkoreatravel.localscore.domain.LocalScore;
import com.realkoreatravel.localscore.repository.LocalScoreRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

/** Local Score 재계산 서비스의 조회·계산·저장 흐름을 검증한다. */
@ExtendWith(MockitoExtension.class)
class LocalScoreServiceTest {

    @Mock
    private LocalScoreRepository localScoreRepository;

    @Mock
    private LocalScoreCalculator localScoreCalculator;

    @InjectMocks
    private LocalScoreService localScoreService;

    @Test
    @DisplayName("Local Score를 재계산하고 저장한다")
    void recalculatesAndSavesLocalScore() {
        LocalScore localScore = LocalScore.builder().totalScore(70).build();
        when(localScoreRepository.findByPlaceId(1L)).thenReturn(Optional.of(localScore));
        when(localScoreCalculator.calculate(localScore)).thenReturn(86);
        when(localScoreRepository.save(localScore)).thenReturn(localScore);

        LocalScore result = localScoreService.recalculate(1L);

        assertThat(result).isSameAs(localScore);
        assertThat(result.getTotalScore()).isEqualTo(86);
        verify(localScoreCalculator).calculate(localScore);
        verify(localScoreRepository).save(localScore);
    }

    @Test
    @DisplayName("Local Score가 없으면 404 예외를 발생시킨다")
    void throwsNotFoundWhenLocalScoreDoesNotExist() {
        // 해당 장소의 Local Score가 없도록 Repository 조회 결과를 빈 Optional로 설정한다.
        when(localScoreRepository.findByPlaceId(1L)).thenReturn(Optional.empty());

        // 재계산을 시도하면 서비스가 404 응답에 사용하는 예외를 발생시키고,
        // 예외 메시지에 Local Score 미존재 사유가 포함되는지 검증한다.
        assertThatThrownBy(() -> localScoreService.recalculate(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Local Score를 찾을 수 없습니다.");

        // 예외가 발생하기 전 장소 ID 기준 조회가 실제로 호출됐는지 확인한다.
        verify(localScoreRepository).findByPlaceId(1L);
    }
}
