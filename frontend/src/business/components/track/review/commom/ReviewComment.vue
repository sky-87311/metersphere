<template>
  <div v-loading="result.loading">
    <div class="comment-list">
      <review-comment-item v-for="(comment,index) in comments" :key="index" :comment="comment"/>
      <div v-if="comments.length === 0" style="text-align: center">
        <i class="el-icon-chat-line-square" style="font-size: 15px;color: #8a8b8d;">
        <span style="font-size: 15px; color: #8a8b8d;">
          {{ $t('test_track.comment.no_comment') }}
        </span>
        </i>
      </div>
    </div>
    <div>
      <el-input
        type="textarea"
        :placeholder="$t('test_track.comment.send_comment')"
        v-model="textarea"
        maxlength="60"
        show-word-limit
        resize="none"
        :autosize="{ minRows: 4, maxRows: 4}"
        @keyup.ctrl.enter.native="sendComment"
      >
      </el-input>
      <el-button type="primary" size="mini" class="send-btn" @click="sendComment">
        {{ $t('test_track.comment.send') }}
      </el-button>
    </div>
  </div>
</template>

<script>
import ReviewCommentItem from "./ReviewCommentItem";

export default {
  name: "ReviewComment",
  components: {ReviewCommentItem},
  props: {
    caseId: String,
    comments: Array
  },
  data() {
    return {
      result: {},
      textarea: '',
    }
  },
  methods: {
    sendComment() {
      let comment = {};
      comment.caseId = this.caseId;
      comment.description = this.textarea;
      if (!this.textarea) {
        this.$warning(this.$t('test_track.comment.description_is_null'));
        return;
      }
      this.result = this.$post('/test/case/comment/save', comment, () => {
        this.$success(this.$t('test_track.comment.send_success'));
        this.$emit('getComments');
        this.textarea = '';
      });
    },
  }
}
</script>

<style scoped>
.send-btn {
  margin-top: 5px;
  width: 100%;
}

.comment-list {
  overflow-y: scroll;
  height: calc(70vh);
}
</style>
