package org.likelion.likelioncrudexcepvalid.post.application;

import org.likelion.likelioncrudexcepvalid.common.error.ErrorCode;
import org.likelion.likelioncrudexcepvalid.common.exception.NotFoundException;
import org.likelion.likelioncrudexcepvalid.member.domain.Member;
import org.likelion.likelioncrudexcepvalid.member.domain.repository.MemberRepository;
import org.likelion.likelioncrudexcepvalid.post.api.dto.request.PostSaveReqDto;
import org.likelion.likelioncrudexcepvalid.post.api.dto.request.PostUpdateReqDto;
import org.likelion.likelioncrudexcepvalid.post.api.dto.response.PostInfoResDto;
import org.likelion.likelioncrudexcepvalid.post.api.dto.response.PostListResDto;
import org.likelion.likelioncrudexcepvalid.post.domain.Post;
import org.likelion.likelioncrudexcepvalid.post.domain.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public PostService(MemberRepository memberRepository, PostRepository postRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    // Create
    @Transactional
    public PostInfoResDto postSave(PostSaveReqDto postSaveReqDto) {
        Member member = memberRepository.findById(postSaveReqDto.memberId()).orElseThrow(
                () -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage()+ postSaveReqDto.memberId())
        );

        Post post = Post.builder()
                .title(postSaveReqDto.title())
                .contents(postSaveReqDto.contents())
                .member(member)
                .build();

        postRepository.save(post);
        return PostInfoResDto.from(post);
    }

    // Read (작성자에 따른 게시물 리스트)
    public PostListResDto postFindAll(){
        List<Post> posts = postRepository.findAll();

        List<PostInfoResDto> postInfoResDtoList = posts.stream()
                .map(PostInfoResDto::from)
                .toList();

        return PostListResDto.from(postInfoResDtoList);
    }

    //ONE
    public PostInfoResDto postFindById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException(ErrorCode.POST_NOT_FOUND_EXCEPTION,
                        ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage()+postId)
        );

        return PostInfoResDto.from(post);
    }

    public PostListResDto postFindMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.MEMBER_NOT_FOUND_EXCEPTION.getMessage()+memberId)
        );

        List<Post> posts = postRepository.findByMember(member);
        List<PostInfoResDto> postInfoResDtoList = posts.stream()
                .map(PostInfoResDto::from)
                .toList();

        return PostListResDto.from(postInfoResDtoList);
    }

    // 제목으로 검색
    public PostListResDto findByTitle(String title) {
        List<Post> posts = postRepository.findByTitle(title);
        List<PostInfoResDto> postInfoResDtoList = posts.stream()
                .map(PostInfoResDto::from)
                .toList();

        return PostListResDto.from(postInfoResDtoList);
    }

    // 작성자 이름으로 검색
    public PostListResDto findByMemberName(String name) {
        List<Post> posts = postRepository.findByMemberName(name);
        List<PostInfoResDto> postInfoResDtoList = posts.stream()
                .map(PostInfoResDto::from)
                .toList();

        return PostListResDto.from(postInfoResDtoList);
    }

    // 내용으로 검색
    public PostListResDto findByContents(String contents) {
        List<Post> posts = postRepository.findByContents(contents);
        List<PostInfoResDto> postInfoResDtoList = posts.stream()
                .map(PostInfoResDto::from)
                .toList();

        return PostListResDto.from(postInfoResDtoList);
    }

    // Update
    @Transactional
    public PostInfoResDto postUpdate(Long postId, PostUpdateReqDto postUpdateReqDto) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException(ErrorCode.POST_NOT_FOUND_EXCEPTION,
                        ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage() + postId)
        );

        post.update(postUpdateReqDto);
        return PostInfoResDto.from(post);
    }

    // Delete
    @Transactional
    public PostInfoResDto postDelete(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException(ErrorCode.POST_NOT_FOUND_EXCEPTION,
                        ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage() + postId)
        );

        postRepository.delete(post);
        return PostInfoResDto.from(post);
    }
}
