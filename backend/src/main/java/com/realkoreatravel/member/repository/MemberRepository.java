package com.realkoreatravel.member.repository;

import com.realkoreatravel.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByProviderAndProviderId(String provider, String providerId);

    Optional<Member> findByEmail(String email);
}
