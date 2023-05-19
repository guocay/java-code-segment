package com.github.guocay.segment.other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * 科目三 灯光模拟
 * @author aCay
 * @since 2023.05.01
 */
public class LightingSimulation {
    /**
     * 程序中用到了 System.in 对象. 所以必须是单例模式
     */
    public static final LightingSimulation INSTANCE;

    /**
     * 日志对象和一些常量
     */
    private static final Logger logger = LoggerFactory.getLogger(LightingSimulation.class);
    private static final String RUN_TIME_OUT = "java.util.concurrent.TimeoutException";
    private static final String RUN_EXECUTION = "java.util.concurrent.ExecutionException";
    /**
     * 配置信息
     */
    private static final int CONFIG_MAX_TOPIC_TIMEOUT = 5;
    private static final int CONFIG_MAX_ALTERNATE_COUNT = 4;
    private static final int CONFIG_TOTAL_TOPIC = 5;
    /**
     * 拨杆状态 枚举
     */
    private static final int LEVER_NEAR_LIGHT_STATUS = 0;
    private static final int LEVER_PARALLEL_LIGHT_STATUS = -1;
    private static final int LEVER_ALTERNATE_LIGHT_STATUS = CONFIG_MAX_ALTERNATE_COUNT;
    /**
     * 总控状态 枚举
     */
    private static final int MASTER_CLOSE_LIGHT_STATUS = 0;
    private static final int MASTER_CONTOUR_LIGHT_STATUS = 1;
    private static final int MASTER_FRONT_LIGHT_STATUS = 2;
    /**
     * 创建线程池
     */
    private static final ExecutorService THREAD_POOL;
    /**
     * 拉取用户输入答案的线程
     */
    private static final Callable<Operation> PULL_ANSWER;
    /**
     * 灯光模拟考试题库及答案
     */
    @SuppressWarnings("unchecked")
    private static final Entry<String, LightStatus>[] QUESTIONS = new Entry[10];
    /**
     * 随机数生成器
     */
    private final Random random;
    /**
     * 计时器
     */
    private final StopWatch stopWatch;
    /**
     * 记录车辆当前灯光状态
     */
    private final LightStatus currentLightStatus;

    static {
        LightStatus nearLightStatus = new LightStatus(MASTER_FRONT_LIGHT_STATUS, LEVER_NEAR_LIGHT_STATUS, false);
        LightStatus alternateLightStatus = new LightStatus(MASTER_FRONT_LIGHT_STATUS, LEVER_ALTERNATE_LIGHT_STATUS, false);
        LightStatus parallelLightStatus = new LightStatus(MASTER_FRONT_LIGHT_STATUS, LEVER_PARALLEL_LIGHT_STATUS, false);
        LightStatus closeLightStatus = new LightStatus(MASTER_CLOSE_LIGHT_STATUS, LEVER_NEAR_LIGHT_STATUS, false);
        LightStatus emergencyLightStatus = new LightStatus(MASTER_CONTOUR_LIGHT_STATUS, LEVER_NEAR_LIGHT_STATUS, true);
        // 设置题库及答案
        QUESTIONS[0] = new SimpleImmutableEntry<>("请开启前照灯", nearLightStatus);
        QUESTIONS[1] = new SimpleImmutableEntry<>("同方向近距离跟车驾驶", nearLightStatus);
        QUESTIONS[2] = new SimpleImmutableEntry<>("通过有交通信号灯控制的路口", nearLightStatus);
        QUESTIONS[3] = new SimpleImmutableEntry<>("夜间在窄路窄桥与非机动车会车", nearLightStatus);
        QUESTIONS[4] = new SimpleImmutableEntry<>("夜间在有路灯照明良好的道路上行驶", nearLightStatus);
        QUESTIONS[5] = new SimpleImmutableEntry<>("夜间通过没有交通信号灯控制的路口", alternateLightStatus);
        QUESTIONS[6] = new SimpleImmutableEntry<>("夜间超越前方车辆", alternateLightStatus);
        QUESTIONS[7] = new SimpleImmutableEntry<>("夜间在没有路灯或照明条件不良条件下行驶", parallelLightStatus);
        QUESTIONS[8] = new SimpleImmutableEntry<>("路边临时停车", emergencyLightStatus);
        QUESTIONS[9] = new SimpleImmutableEntry<>("关闭所有灯光,开始其他考试", closeLightStatus);
        // 设置拉取答案的线程和线程池
        THREAD_POOL = Executors.newFixedThreadPool(1);
        final Scanner scanner = new Scanner(System.in);
        PULL_ANSWER = () -> Operation.of(scanner.nextLine());
        // 设置单例模式下的实例
        INSTANCE = new LightingSimulation();
    }

    private LightingSimulation(){
        this.random = new Random();
        this.stopWatch = new StopWatch();
        this.currentLightStatus = QUESTIONS[QUESTIONS.length - 1].getValue();
    }
    /**
     * 启动灯光模拟考试
     */
    public void startup(){
        // 加 1 是为了将 "关闭所有灯光" 算进去.
        startup(CONFIG_TOTAL_TOPIC + 1, CONFIG_MAX_TOPIC_TIMEOUT);
    }
    /**
     * 启动灯光模拟考试
     * @param topicTotal 总出题数
     * @param topicTimeout 超时时间
     */
    public void startup(int topicTotal, int topicTimeout){
        // 打印准备信息
        printPrepareMessage();
        // 循环出六道题
        IntStream.range(0, topicTotal).forEach(index -> {
            // 通过 Future 的超时异常, 实现五秒内答题.
            try {
                // 从题库中抽取考题后,打印题目. 并返回期望答案.
                LightStatus els = printQuestions(index);
                // 1. 提交任务, 并指定拉取的最大时间; 2. 将操作追加至当前状态;
                appendOperation(THREAD_POOL.submit(PULL_ANSWER).get(topicTimeout, TimeUnit.SECONDS));
                // 校验状态是否符合要求
                judgmentStatus(els);
            }catch (Exception ex){
                String errorInfo = switch (ex.getClass().getName()){
                    case RUN_TIME_OUT -> "答题超时, 成绩不合格!!!";
                    case RUN_EXECUTION -> "输入值非法, 成绩不合格!!!";
                    default -> "发生未预期错误, 成绩不合格!!!";
                };
                applicationExit(errorInfo);
            }
            // 准备下一轮, 回退远近光交替至近光.
            prepareNextRound();
        });
        logger.info("考试结束, 成绩合格!!!");
        THREAD_POOL.shutdownNow();
    }

    /**
     * 打印准备信息
     */
    private void printPrepareMessage() {
        logger.info("""
                答题时需要输入三个数字,并以空格分开.
                `1. 第一个数字代表灯光总开关拨动次数, 输入 1 代表顺时针拨动一下, -1 代表逆时针拨动一下.
                `2. 第二个数字代表左拨杆拨动次数. 输入 1 代表向近怀拨动一下, -1 代表向远怀拨动一下.
                `3. 第三个数字代表双闪控制开关, 输入正整数. 表示按动几次.
                输入完毕后,必须按回车键提交.
                """
        );
        logger.info("""
                下面将开始模拟夜间灯光的考试!!!
                请在嘀声后五秒内完成答题.
                """
        );
    }
    /**
     * 打印当前的灯光状态
     */
    private void printCurrentStatus() {
        // 获取总灯光状态
        String master = switch (currentLightStatus.master){
            case MASTER_CLOSE_LIGHT_STATUS -> "关闭";
            case MASTER_CONTOUR_LIGHT_STATUS -> "示廓灯";
            default -> "前照灯";
        };
        // 获取左拨杆状态
        String lever = switch (currentLightStatus.lever){
            case LEVER_PARALLEL_LIGHT_STATUS -> "远光灯";
            case LEVER_NEAR_LIGHT_STATUS -> currentLightStatus.master == MASTER_CLOSE_LIGHT_STATUS ? "关闭" : "近光灯";
            default -> "远近光交替";
        };
        // 获取双闪灯状态
        String emergency = currentLightStatus.emergency ? "开启" : "关闭";
        logger.info("""
                操作耗时: {}毫秒.
                操作后的灯: 总控[{}], 拨杆[{}], 双闪[{}].

                """,
                stopWatch.end(), master, lever, emergency
        );
    }
    /**
     * 准备下一轮, 回退远近光交替至近光.
     */
    private void prepareNextRound() {
        currentLightStatus.lever = min(LEVER_NEAR_LIGHT_STATUS, currentLightStatus.lever);
    }
    /**
     * 程序退出, 打印提示语
     * @param msg 提示信息
     */
    private void applicationExit(String msg) {
        logger.error(msg);
        THREAD_POOL.shutdownNow();
        System.exit(0);
    }
    /**
     * 校验状态是否符合要求
     * @param expectationLightStatus 期望灯光状态
     */
    private void judgmentStatus(LightStatus expectationLightStatus) {
        if (!currentLightStatus.equals(expectationLightStatus)){
            applicationExit("答案错误, 成绩不合格!!!");
        }
    }
    /**
     * 将操作追加至当前状态
     * @param operation 动作
     */
    private void appendOperation(Operation operation) {
        currentLightStatus.setMaster(operation.master);
        currentLightStatus.setLever(operation.lever);
        currentLightStatus.setEmergency(operation.emergency);
        // 打印当前灯光状态
        printCurrentStatus();
    }
    /**
     * 打印题目并 返回期望的目标灯光
     * @param index 第几题
     * @return 期望的目标灯光
     */
    private LightStatus printQuestions(int index) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        Entry<String, LightStatus> question = (switch (index){
            case 0 -> QUESTIONS[0];
            case CONFIG_TOTAL_TOPIC -> QUESTIONS[QUESTIONS.length - 1];
            default -> {
                Entry<String, LightStatus> _temp = null;
                while (_temp == null){
                    // 加 1 是为了 错开下标为 0 的题.
                    int num = random.nextInt(7) + 1;
                    _temp = QUESTIONS[num];
                    QUESTIONS[num] = null;
                }
                yield _temp;
            }
        });
        logger.info(question.getKey());
        TimeUnit.SECONDS.sleep(1);
        logger.info("--- 嘀 ---");
        stopWatch.begin();
        return question.getValue();
    }
    /**
     * 操作内容, 答卷
     */
    private record Operation(int master, int lever, int emergency) {
        /**
         * 解析输入, 生成操作
         * @param input 输入字符
         * @return 操作
         */
        public static Operation of(String input) {
            String[] info = input.split(" ");
            return new Operation(Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2]));
        }
    }
    /**
     * 灯光状态
     */
    private static class LightStatus {
        /**
         * 总开关
         * <li>0: 代表关闭总开关</li>
         * <li>1: 代表开启示廓灯</li>
         * <li>2: 代表开启前照灯</li>
         */
        private int master;
        /**
         * 左拨杆
         * <li>-1: 代表开启远光灯</li>
         * <li>0: 代表开启近光灯</li>
         * <li>1: 开启远近交替</li>
         */
        private int lever;
        /**
         * 双闪灯
         * <li>true: 开启双闪灯</li>
         * <li>false: 关闭双闪灯</li>
         */
        private boolean emergency;

        private LightStatus(int master, int lever, boolean emergency) {
            this.master = master;
            this.lever = lever;
            this.emergency = emergency;
        }

        public void setMaster(int master) {
            // 如果操作后的结果大于2, 则回退至2.
            // 如果操作后的结果小于0, 则回退至0.
            this.master = max(min(this.master + master, MASTER_FRONT_LIGHT_STATUS), MASTER_CLOSE_LIGHT_STATUS);
        }

        public void setLever(int lever) {
            // 如果操作后的结果小于 -1, 则回退至 -1.
            // 如果操作后的结果大于 4, 则回退至 4.
            this.lever = max(min(this.lever + lever, CONFIG_MAX_ALTERNATE_COUNT), LEVER_PARALLEL_LIGHT_STATUS);
        }

        public void setEmergency(int emergency) {
            // 判断操作的次数是奇数还是偶数, 是奇数则不改变灯光状态. 反之则改变状态.
            this.emergency = (emergency % 2 == 0) == this.emergency;
        }
        @Override
        public boolean equals(Object o) {
            return this == o ||
                    (o instanceof LightStatus that
                            && master == that.master
                            && emergency == that.emergency
                            && lever == that.lever);
        }
    }

    /**
     * 计时器
     */
    private static class StopWatch {
        /**
         * 记录当前时间
         */
        private Long currentTimeMillis;
        /**
         * 计时器开始工作.
         */
        public void begin(){
            this.currentTimeMillis = System.currentTimeMillis();
        }
        /**
         * 计数器结束工作, 并返回持续时间.
         * @return 返回时间间隔
         */
        public Long end(){
            return currentTimeMillis == null ? null : System.currentTimeMillis() - currentTimeMillis;
        }
    }

    public static void main(String[] args) {
        INSTANCE.startup();
        // INSTANCE.startup(5, 5);
    }
}
