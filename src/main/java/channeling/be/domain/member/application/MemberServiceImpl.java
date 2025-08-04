package channeling.be.domain.member.application;

import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.repository.MemberRepository;
import channeling.be.domain.member.presentation.MemberConverter;
import channeling.be.domain.member.presentation.MemberResDTO;
import channeling.be.global.infrastructure.aws.S3Service;
import channeling.be.global.infrastructure.aws.S3Service;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.handler.MemberHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static channeling.be.domain.member.presentation.MemberReqDTO.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberServiceImpl implements MemberService {
	private final MemberRepository memberRepository;
	private final S3Service s3Service;

	@Transactional
	@Override
	public MemberResDTO.updateSnsRes updateSns(updateSnsReq updateSnsReq) {
		//TODO: 자신의 정보를 수정하는 것인지 확인 필요
		Long memberId = 1L; // 임시로 1L로 설정, 실제로는 인증된 멤버의 ID를 사용해야 함
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
		member.updateSnsLinks(
			updateSnsReq.instagramLink(),
			updateSnsReq.tiktokLink(),
			updateSnsReq.facebookLink(),
			updateSnsReq.twitterLink()
		);

		return MemberConverter.toUpdatedSns(
			member.getInstagramLink(),
			member.getTiktokLink(),
			member.getFacebookLink(),
			member.getTwitterLink()
		);
	}

	@Transactional
	@Override
	public MemberResDTO.updateProfileImageRes updateProfileImage(Member loginMember, ProfileImageUpdateReq updateProfileImageReq) {
		// 멤버 조회 -> 실제 DB 조회로 영속화
		Member member = memberRepository.findByGoogleId(loginMember.getGoogleId()).orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
		// 새로운 사진 s3에 업로드 후, url 얻기
		String uploadedUrl = s3Service.uploadImage(updateProfileImageReq.image());
		// 엔티티 업데이트 -> 더티 체킹
		member.profileImage(uploadedUrl);
		return MemberConverter.toUpdatedProfileImage(uploadedUrl);
	}

	@Override
	@Transactional
    public Member findOrCreateMember(String googleId, String email, String nickname, String profileImage) {
		return memberRepository.findByGoogleId(googleId)
			.orElseGet(() -> memberRepository.save(
				Member.builder()
					.googleId(googleId)
					.googleEmail(email)
					.nickname(nickname)
					.profileImage(profileImage)
					.build()
			));
	}


	@Override
	public MemberResDTO.getMemberInfo getMemberInfo(Member loginMember) {
		Member member = memberRepository.findById(loginMember.getId())
				.orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
		return MemberConverter.toGetMemberInfo(member);
	}

}
