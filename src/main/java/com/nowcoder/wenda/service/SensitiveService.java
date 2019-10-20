package com.nowcoder.wenda.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LuoJiaQi
 * @Date 2019/10/15
 * @Time 2:21
 */

@Service
public class SensitiveService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveService.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            //从文本中读取信息到字典树
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                addWord(lineTxt.trim());
            }
            reader.close();
        } catch (Exception e) {
            LOGGER.error("读取敏感词文件失败" + e.getMessage());
        }
    }

    //在字典树中增加关键词
    private void addWord(String lineTxt) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < lineTxt.length(); i++) {
            Character c = lineTxt.charAt(i);
            if (isSymbol(c)) {//如果遇到特殊字符就直接跳过
                continue;
            }

            TrieNode node = tempNode.getSubNode(c);

            if (node == null) {
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == lineTxt.length() - 1) {
                tempNode.setKeywordEnd(true);

            }
        }
    }

    private class TrieNode {
        //表示是不是某一个敏感词的结尾
        private boolean end = false;

        //当前结点下的所有自己子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }


        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        boolean isKeywordEnd() {
            return end;
        }

        void setKeywordEnd(boolean end) {
            this.end = end;
        }
    }

    private TrieNode rootNode = new TrieNode();

    //判断是否是特殊字符
    private boolean isSymbol(char c) {
        int ic = (int) c;
        //东亚文字 0x2E80-0x9FFF
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }



    //敏感词过滤，打码
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        StringBuilder result = new StringBuilder();

        String replacement = "***";
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;

        while (position < text.length()) {
            char c = text.charAt(position);

            if (isSymbol(c)) {//如果c是特殊字符
                if (tempNode == rootNode) {//如果是刚开始匹配，那就要记录
                    result.append(c);//这里如果不append(c)，那么不在敏感词中间的特殊字符会被误伤
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            if (tempNode == null) {
                result.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                result.append(replacement);
                position++;
                begin = position;
                tempNode = rootNode;
            } else {
                position++;
            }
        }

        result.append(text.substring(begin));
        return result.toString();
    }


    //打包时去掉多余main函数，故改名
    public static void miain(String[] args) {
        SensitiveService s = new SensitiveService();
        s.addWord("色情");
        s.addWord("赌博");
        System.out.println(s.filter("hi 你好💣色💣情"));
        System.out.println(s.filter("hi 你好色_情"));
        System.out.println(s.filter("hi 你好色h情"));

    }
}