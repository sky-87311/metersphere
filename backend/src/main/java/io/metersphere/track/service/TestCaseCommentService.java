package io.metersphere.track.service;

import io.metersphere.base.domain.*;
import io.metersphere.base.mapper.TestCaseCommentMapper;
import io.metersphere.base.mapper.TestCaseMapper;
import io.metersphere.base.mapper.TestCaseReviewMapper;
import io.metersphere.base.mapper.UserMapper;
import io.metersphere.commons.utils.SessionUtils;
import io.metersphere.notice.service.MailService;
import io.metersphere.track.request.testreview.SaveCommentRequest;
import io.metersphere.track.request.testreview.SaveTestCaseReviewRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class TestCaseCommentService {

    @Resource
    TestCaseCommentMapper testCaseCommentMapper;
    @Resource
    private TestCaseReviewMapper testCaseReviewMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    MailService mailService;
    @Resource
    TestCaseMapper testCaseMapper;

    public void saveComment(SaveCommentRequest request) {
        TestCaseComment testCaseComment = new TestCaseComment();
        testCaseComment.setId(UUID.randomUUID().toString());
        testCaseComment.setAuthor(SessionUtils.getUser().getId());
        testCaseComment.setCaseId(request.getCaseId());
        testCaseComment.setCreateTime(System.currentTimeMillis());
        testCaseComment.setUpdateTime(System.currentTimeMillis());
        testCaseComment.setDescription(request.getDescription());
        testCaseCommentMapper.insert(testCaseComment);
        TestCaseWithBLOBs testCaseWithBLOBs;
        testCaseWithBLOBs = testCaseMapper.selectByPrimaryKey(request.getCaseId());
        SaveTestCaseReviewRequest caseReviewRequest = new SaveTestCaseReviewRequest();
        List<String> userIds = new ArrayList<>();
        userIds.add(testCaseWithBLOBs.getMaintainer());
        mailService.sendHtml(userIds, "comment", caseReviewRequest, request, testCaseWithBLOBs);

    }

    public List<TestCaseComment> getComments(String caseId) {
        TestCaseCommentExample testCaseCommentExample = new TestCaseCommentExample();
        testCaseCommentExample.setOrderByClause("update_time desc");
        testCaseCommentExample.createCriteria().andCaseIdEqualTo(caseId);
        List<TestCaseComment> testCaseComments = testCaseCommentMapper.selectByExampleWithBLOBs(testCaseCommentExample);
        testCaseComments.forEach(testCaseComment -> {
            String authorId = testCaseComment.getAuthor();
            User user = userMapper.selectByPrimaryKey(authorId);
            testCaseComment.setAuthor(user.getName());
        });
        return testCaseComments;
    }

    public void deleteComment(String caseId) {
        TestCaseCommentExample testCaseCommentExample = new TestCaseCommentExample();
        testCaseCommentExample.createCriteria().andCaseIdEqualTo(caseId);
        testCaseCommentMapper.deleteByExample(testCaseCommentExample);
    }
}
