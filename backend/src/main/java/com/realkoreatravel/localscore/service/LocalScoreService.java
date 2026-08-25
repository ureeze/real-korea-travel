package com.realkoreatravel.localscore.service;

import com.realkoreatravel.localscore.domain.LocalScore;
import com.realkoreatravel.localscore.repository.LocalScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Local Score 조회와 종합 점수 재계산·저장을 담당한다. */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocalScoreService {

    private final LocalScoreRepository localScoreRepository;
    private final LocalScoreCalculator localScoreCalculator;

    /** 장소 ID로 Local Score를 조회하고 세부 점수 기준으로 종합 점수를 재계산해 저장한다. */
    @Transactional
    public LocalScore recalculate(Long placeId) {
        LocalScore localScore = localScoreRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Local Score를 찾을 수 없습니다."
                ));
        localScore.updateTotalScore(localScoreCalculator.calculate(localScore));
        return localScoreRepository.save(localScore);
    }
}
