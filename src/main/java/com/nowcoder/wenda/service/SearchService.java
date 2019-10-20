package com.nowcoder.wenda.service;

import com.nowcoder.wenda.model.Question;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LuoJiaQi
 * @Date 2019/10/19
 * @Time 19:31
 */

@Service
public class SearchService {

    //指定solr的url
    private static final String SOLR_URL = "http://127.0.0.1:8983/solr/wenda";

    //创建一个client
    private HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();

    private static final String QUESTION_TITLE_FIELD = "question_title";
    private static final String QUESTION_CONTENT_FIELD = "question_content";

    /**
     * 查询功能
     * @param keyword 查询关键词
     * @param offset 翻页用
     * @param count 翻页用
     * @param hlPre 高亮前缀
     * @param hlPos 高亮后缀
     * @return 查询得到的问题列表
     */
    public List<Question> searchQuestion(String keyword, int offset, int count,
                                         String hlPre, String hlPos) throws Exception {
        List<Question> questionList = new ArrayList<>();
        //构造solrquery
        SolrQuery query = new SolrQuery(keyword);

        //query设置
        query.setRows(count);//有多少行
        query.setStart(offset);

        query.setHighlight(true);//开启高亮
        query.setHighlightSimplePre(hlPre);//设置高亮前缀
        query.setHighlightSimplePost(hlPos);//设置高亮后缀
        //hl.f1是solr高亮的设置，在dashboard上有，下面意思是对title和content高亮
        query.set("hl.fl", QUESTION_TITLE_FIELD + "," + QUESTION_CONTENT_FIELD);

        // 执行query请求
        QueryResponse response = client.query(query);

        // 高亮的文本中有的是title里的，有的是content里的，要把它们区分开来
        // 高亮的结果是存放在一个大map里的
        // key是questionId，value是一个map（key是question_title或者question_content,value是一个list，就是高亮的结果）
        for (Map.Entry<String, Map<String, List<String>>> entry : response.getHighlighting().entrySet()) {
            Question question = new Question();
            question.setId(Integer.parseInt(entry.getKey()));

            // 对question_title和question_content区分
            if (entry.getValue().containsKey(QUESTION_TITLE_FIELD)) {
                List<String> titleList = entry.getValue().get(QUESTION_TITLE_FIELD);
                if (titleList.size() > 0) {
                    question.setTitle(titleList.get(0));//为什么是0，其实好像就只有一条。。
                }
            }
            if (entry.getValue().containsKey(QUESTION_CONTENT_FIELD)) {
                List<String> contentList = entry.getValue().get(QUESTION_CONTENT_FIELD);
                if (contentList.size() > 0) {
                    question.setContent(contentList.get(0));
                }
            }
            //这里add进去的question只设置了三个属性：id，questionTitle，questionContent
            questionList.add(question);
        }
        return questionList;
    }

    /**
     * 建立索引。当发表新的问题时需要建立索引，因为只对title和content检索，所以只需要三个参数
     * @param qid
     * @param title
     * @param content
     * @return 是否建立成功
     * @throws Exception
     */
    public boolean indexQuestion(int qid, String title, String content) throws Exception{
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id", qid);
        doc.setField(QUESTION_TITLE_FIELD, title);
        doc.setField(QUESTION_CONTENT_FIELD, content);
        UpdateResponse response = client.add(doc, 1000);//1000ms内返回
        return response != null && response.getStatus() == 0;
    }
}