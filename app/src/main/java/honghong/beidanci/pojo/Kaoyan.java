package honghong.beidanci.pojo;

public class Kaoyan {
    // 编号
    private Integer id;
    // 英文单词
    private String word;
    // 音标
    private String yinbiao;
    // 释义
    private String mean;
    // 权值
    /**
     * 权值 默认为100，主要有加权和降权两个变化
     * 降权：当该单词做过并正确后，权值降为10，降低单词出现概率
     * 加权：当该单词做过并错误之后，权值升为10000，提升单词出现概率
     * 对所有单词权值求和，对总权值取随机数，遍历找到该值对应的单词
     */
    private Integer weight;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getYinbiao() {
        return yinbiao;
    }

    public void setYinbiao(String yinbiao) {
        this.yinbiao = yinbiao;
    }

    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
