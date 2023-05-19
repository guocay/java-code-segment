package com.github.guocay.algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 乐观系数计算法
 * @author GuoCay
 * @since 2022/5/31
 */
public class OptimisticCoefficient {

	private static final Logger LOGGER = LoggerFactory.getLogger(OptimisticCoefficient.class);

    /**
     * 允许的最小权限合
     */
    private static final Double MIN_WEIGHT = 0.5D;

    /**
     * 有效区间需覆盖的最小投票数
     */
    private static final Integer VALID_RANGE_MIN_VOTER = 2;

    /**
     * 是否 DEBUG 模式
     */
    private Boolean isDebug = false;

    /**
     * 存储投票者与选票
     */
    private final List<Voter> voters;

    /**
     * 最小值
     */
    private final Double min;

    /**
     * 最大值
     */
    private final Double max;

    /**
     * 创建计算法实体, 传入投票者信息
     * @param voters 投票者
     */
    public OptimisticCoefficient(Voter... voters){
        List<Voter> list = Arrays.asList(voters);
        checkVoterWeight(list);
        this.voters = list;
        this.min = findMin(list);
        this.max = findMax(list);
    }

    /**
     * 查找最大值
     * @param list 集合
     * @return 最大值
     */
    private Double findMax(List<Voter> list) {
        return list.stream().map(Voter::getVote).mapToDouble(Vote::getMax).summaryStatistics().getMax();
    }

    /**
     * 查找最小值
     * @param list 集合
     * @return 最小值
     */
    private Double findMin(List<Voter> list) {
        return list.stream().map(Voter::getVote).mapToDouble(Vote::getMin).summaryStatistics().getMin();
    }

    /**
     * 开启 debug 模式
     * @return 当前对象
     */
    public OptimisticCoefficient openDebug(){
        this.isDebug = true;
        return this;
    }

    /**
     * 执行计算
     * @param coincide 重合长度
     * @param pellet 颗粒度
     * @return 得分
     */
    public Double compute(Integer coincide, Pellet pellet){
        // 校验入参是否有效
        checkCoincide(coincide, pellet);
        // 优化范围精度, 声明中间参数
        printInfo("准备: 开始计算, 声明辅助指标数据...");
        Double minRange = format(min, pellet);
        printInfo("总区间最小值为: %f", minRange);
        Double maxRange = format(max, pellet);
        printInfo("总区间最大值为: %f", maxRange);
        Double pelletValue = pellet.getPellet();
        printInfo("区间颗粒度为: %f", pelletValue);

        // 1. 找到所有专家评分中的最大值和最小值转换成一个一个点的集合.
        printInfo("\n第一步: 构建投票总区间...");
        List<Set<Long>> allRange = buildTotalRange(maxRange, minRange, pelletValue);

        // 2. 将每个专家的评分区间转换成一个一个点的集合.
        printInfo("\n第二步: 构建投票者全量的投票颗粒度...");
        Map<Long, List<Double>> allVoteMap = buildVoteMap(pellet);

        // 3. 将第二步的集合映射到第一步的累加器集合.
        printInfo("\n第三步: 将投票者数据映射至全量区间...");
        mappingVoteRange(allVoteMap, allRange, pelletValue, minRange);

        // 4. 拿出符合区间的所有集合数据.
        printInfo("\n第四步: 拿出符合区间要求的集合数据");
        List<Set<Long>> rangeList = getValidRangeData(allRange, coincide);

        // 5. 计算哪个区间的权重最大.
        printInfo("\n第五步: 计算各区间的得票数据");
        List<Double> collectWeight = calculationVoteRangeWeight(rangeList);
        int maxWeightValueIndex = getAndCheckVoteRangeWeight(collectWeight);

        // 6. 获取有效的投票数据, 重新拉取过滤投票者
        printInfo("\n第六步: 重新拉取有效的投票者列表");
        List<Voter> validVoter = rebuildVoter(rangeList, maxWeightValueIndex);

        // 7. 为投票者重新计算权重, 计算投票者乐观系数
        printInfo("\n第七步: 重新计算有效投票者的权重");
        Double validMaxWeight = collectWeight.get(maxWeightValueIndex);
        rebuildVoterWeight(validVoter, validMaxWeight);

        // 8. 为投票者计算出乐观系数
        printInfo("\n第八步: 计算有效投票者的乐观系数");
        buildVoterOptimistic(validVoter, minRange, maxWeightValueIndex, coincide, pelletValue);

        // 9.运算获取最后结果
        printInfo("\n第九步: 计算出投票者在乐观系数法下的出分");
        return format(calculationVoterFraction(validVoter), pellet);
    }

    /**
     * 计算出有效投票者对本次的出分
     * @param validVoter 有效投票者
     * @return 本次分数
     */
    private Double calculationVoterFraction(List<Voter> validVoter) {
        return validVoter.stream().mapToDouble(Voter::validFraction).summaryStatistics().getSum();
    }

    /**
     * 计算投票者乐观系数
     * @param minRange 总区间最小值
     * @param coincide 重合区间长度
     * @param pelletValue 颗粒度
     * @param validVoter 有效投票者
     * @param maxWeightValueIndex 有效的最大权重
     * @return 有效投票者
     */
    private List<Voter> buildVoterOptimistic(List<Voter> validVoter, Double minRange, Integer maxWeightValueIndex,
                                      Integer coincide, Double pelletValue) {
        double min = minRange + pelletValue * maxWeightValueIndex;
        printInfo(">> 本次运算有效区间最小值为: %f", min);
        double max = min + coincide * pelletValue;
        printInfo(">> 本次运算有效区间最大值为: %f", max);
        validVoter.forEach(item -> {
            Vote vote = item.getVote();
            // 乐观度
            double optimism = vote.max - max;
            // 总偏移度
            double offset = optimism + (min - vote.min);
            item.setOptimism(offset == 0 ? 0.5D : optimism / offset);
            printInfo("投票者 %s 的总偏移度为: %f, 乐观度为: %f, 乐观比例系数为: %f", item.getId(), offset, optimism, item.getOptimism());
        });
        return validVoter;
    }

    /**
     * 重新计算投票者在投票组中的新权重
     * @param validVoter 有效投票者
     * @param validMaxWeight 有效的最大权重
     * @return 有效投票者
     */
    private List<Voter> rebuildVoterWeight(List<Voter> validVoter, Double validMaxWeight) {
        validVoter.forEach(item -> {
            item.setValidWeight(item.getWeight() / validMaxWeight);
            printInfo("投票者 %s 的新权重为: %f", item.getId(), item.getValidWeight());
        });
        return validVoter;
    }

    /**
     * 重新拉取过滤投票者列表
     * @param rangeList 区间集合
     * @param maxWeightValueIndex 最大权限区间相对所在集合的索引
     * @return 有效的投票者集合
     */
    private List<Voter> rebuildVoter(List<Set<Long>> rangeList, int maxWeightValueIndex) {
        Set<Long> validVoterIds = rangeList.get(maxWeightValueIndex);
        return voters.stream()
                .filter(item -> validVoterIds.contains(item.getId()))
                .peek(item -> printInfo("重新拉取的投票者为: %s", item.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取并检查集合内最大权重的区间所在索引
     * @param collectWeight 权重集合
     * @return 最大权重的索引
     */
    private int getAndCheckVoteRangeWeight(List<Double> collectWeight) {
        double maxWeightValue = Collections.max(collectWeight);
        if(maxWeightValue <= MIN_WEIGHT){
            stop("重合区权重不得小于最小权重和 ");
        }
        int maxWeightValueIndex = collectWeight.indexOf(maxWeightValue);
        printInfo("集合中最大权重区间所在索引为: %d", maxWeightValueIndex);
        return maxWeightValueIndex;
    }

    /**
     * 计算各有效投票区间的权重
     * @param rangeList 有效投票区间集合
     * @return 区间集合权重
     */
    private List<Double> calculationVoteRangeWeight(List<Set<Long>> rangeList) {
        return rangeList.stream()
                .map(item -> {
                    if(item.size() < VALID_RANGE_MIN_VOTER){
                        return Double.MIN_VALUE;
                    }
                    return item.stream().mapToDouble(id -> findVoterById(id).getWeight()).summaryStatistics().getSum();
                })
                .peek(item -> printInfo("全量区间内区段权重总和为: %f", item))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有的有效投票区间数据
     * @param allRange 全量区间
     * @param coincide 偏移大小
     * @return 有效数据
     */
    private List<Set<Long>> getValidRangeData(List<Set<Long>> allRange, Integer coincide) {
        int rangeSize = allRange.size();
        List<Set<Long>> rangeList = new ArrayList<>();
        IntStream.range(0, rangeSize).limit(rangeSize - coincide + 1).forEach(index -> {
            // 取出 区间内的投票者数
            Set<Long> collect = allRange.stream().limit(index + coincide).skip(index)
                    .flatMap(Set::stream).collect(Collectors.toSet());
            // 组装数据
            rangeList.add(collect);
            printInfo("第%d个有效的投票区间为: %s", index, collect);
        });
        return rangeList;
    }

    /**
     * 将投票者的数据映射到全量区间
     * @param allVoteMap 投票
     * @param allRange 全量区间
     * @param pelletValue 颗粒度
     * @param minRange 区间最小值
     */
    private List<Set<Long>> mappingVoteRange(Map<Long, List<Double>> allVoteMap, List<Set<Long>> allRange, Double pelletValue, Double minRange) {
        allVoteMap.forEach((key, values) -> {
            for (Double value : values) {
                int offset = (int) Math.round((value - minRange) / pelletValue);
                allRange.get(offset).add(key);
            }
        });
        allRange.forEach(item -> printInfo("全量区间中相对偏移位置的得票数据是: %s", item));
        return allRange;
    }

    /**
     * 构建投票者全量的投票颗粒度
     * @param pellet 颗粒度
     * @return 全量投票
     */
    private Map<Long, List<Double>> buildVoteMap(Pellet pellet) {
        Double pelletValue = pellet.getPellet();
        return voters.stream().collect(Collectors.toMap(Voter::getId, item -> {
            Vote vote = item.getVote();
            List<Double> voteList = new ArrayList<>();
            double assist = vote.getMin();
            do {
                voteList.add(format(assist, pellet));
            } while ((assist += pelletValue) <= vote.getMax());
            printInfo("投票者 %s 的全部投票为: %s", item.getId(), voteList);
            return voteList;
        }));
    }

    /**
     * 构建总区间
     * @param maxRange 区间最大值
     * @param minRange 区间最小值
     * @param pelletValue 区间颗粒度
     * @return 区间
     */
    private List<Set<Long>> buildTotalRange(Double maxRange, Double minRange, Double pelletValue) {
        int rangeSize = (int) ((maxRange - minRange) / pelletValue + 1);
        printInfo("总区间的长度为: %d", rangeSize);
        List<Set<Long>> allRange = new ArrayList<>(rangeSize);
        IntStream.range(0, rangeSize).forEach(index -> allRange.add(new HashSet<>()));
        return allRange;
    }

    /**
     * 根据id 查找投票者
     * @param id 投票者ID
     * @return 投票者
     */
    private Voter findVoterById(Long id) {
        for (Voter voter : voters) {
            if (voter.getId().equals(id)){
                return voter;
            }
        }
        throw new RuntimeException("未查找到当前投票者 ");
    }

    /**
     * 根据颗粒度,格式化数据
     * @param num 待格式化数据
     * @param pellet 颗粒度
     * @return 格式化以后的数据
     */
    private Double format(Double num, Pellet pellet){
        return BigDecimal.valueOf(num).setScale(pellet.getValid(), RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 校验投票者权重想加是否大于0.5
     * @param list 集合
     */
    private void checkVoterWeight(List<Voter> list) {
        // 判断总权重不得超过 1
        stop(list.stream().mapToDouble(Voter::getWeight).sum() > 1, "总权重之和不得超过1.0");
        // 判断投票者ID不得重复
        Set<Long> collect = list.stream().map(Voter::getId).collect(Collectors.toSet());
        stop(collect.size() < list.size(), "投票者ID不得出现重复");
    }

    /**
     * 校验重合区间需在指定范围内
     * @param coincide 重合长度
     * @param pellet 颗粒度
     */
    private void checkCoincide(Integer coincide, Pellet pellet) {
        // 判断入参的重合区间取值范围需大于0且小于等于1
        double coincideRange = coincide.doubleValue() * pellet.getPellet();
        stop(coincideRange <= 0 || coincideRange > 1, "重合区间长度不得大于1或小于0");
    }

    /**
     * 终止计算,并打印异常信息
     * @param message 异常信息
     */
    protected static void stop(String message){
        throw new RuntimeException(message);
    }

    /**
     * 终止计算,并打印异常信息
     * @param expression 表达式
     * @param message 异常信息
     */
    protected static void stop(Boolean expression, String message){
        if (expression) {
            stop(message);
        }
    }

    /**
     * 打印信息
     * @param format 格式化
     * @param args 参数
     */
    protected void printInfo(String format, Object... args){
        if(isDebug){
            System.out.printf(format + "\n", args);
        }
    }

	/**
	 * 投票者信息
	 */
	public static class Voter {

		/**
		 * 投票者主键
		 */
		private final Long id;

		/**
		 * 投票者权重
		 */
		private final Double weight;

		/**
		 * 投票者有效权重
		 */
		private Double validWeight;

		/**
		 * 投票者乐观系数
		 */
		private Double optimism;

		/**
		 * 票据
		 */
		private Vote vote;

		/**
		 * 创建投票者
		 * @param id 投票者注解
		 * @param weight 权重, 不得大于1.0.
		 */
		public Voter(Long id, Double weight) {
			// 判断权重取值必须在(0,1)集合之间
			OptimisticCoefficient.stop(weight <= 0 || weight > 1, "投票者权重取值必须在(0,1)集合之间 ");
			this.id = id;
			this.weight = weight;
		}

		/**
		 * 创建投票者
		 * @param id 投票者注解
		 * @param weight 权重, 不得大于1.0.
		 * @param vote 得分
		 */
		public Voter(Long id, Double weight, Vote vote){
			this(id, weight);
			this.vote = vote;
		}

		/**
		 * 创建投票者
		 * @param id 投票者注解
		 * @param weight 权重, 不得大于1.0.
		 * @param min 最小得分
		 * @param max 最大得分
		 */
		public Voter(Long id, Double weight, Double min, Double max){
			this(id,weight, new Vote(min, max));
		}

		public Long getId() {
			return id;
		}

		public Double getWeight() {
			return weight;
		}

		public Vote getVote() {
			return vote;
		}

		public Double getValidWeight() {
			return validWeight;
		}

		public void setValidWeight(Double validWeight) {
			this.validWeight = validWeight;
		}

		public Double getOptimism() {
			return optimism;
		}

		public void setOptimism(Double optimistic) {
			this.optimism = optimistic;
		}

		/**
		 * 计算当前投票者的最终有效出分
		 * @return 分数
		 */
		public Double validFraction() {
			// 1. 有效权重必须存在
			OptimisticCoefficient.stop(Objects.isNull(validWeight) || validWeight > 1 || validWeight <= 0,
				"投票者的有效权重不在合理范围内");
			// 2. 乐观系数必须存在
			OptimisticCoefficient.stop(Objects.isNull(optimism) || optimism > 1 || optimism <= 0,
				"投票者的乐观系数不在合理范围内");
			// 乐观得分
			double optimisticValue = vote.max * optimism;
			// 悲观得分
			double pessimismValue = vote.min * (1 - optimism);
			// 最终得分
			return (pessimismValue + optimisticValue) * validWeight;
		}
	}

	/**
	 * 选票信息
	 */
	public static class Vote {

		/**
		 * 最小得分
		 */
		public final Double min;

		/**
		 * 最大得分
		 */
		public final Double max;

		public Vote(Double min, Double max) {
			// 判断得分区间不得超过1
			OptimisticCoefficient.stop(max < min, "得分最大值不得小于最小值 ");
			OptimisticCoefficient.stop(max - min > 1, "得分最大值与最小值之间不得大于1.0 ");
			this.max = max;
			this.min = min;
		}

		public Double getMin() {
			return min;
		}

		public Double getMax() {
			return max;
		}
	}

	/**
	 * 计算颗粒度
	 */
	public enum Pellet {

		/**
		 * 十分之一
		 */
		ONE_TENTH(0.1D),

		/**
		 * 百分之一
		 */
		ONE_HUNDRED(0.01D);

		private final Double pellet;

		Pellet(Double pellet) {
			this.pellet = pellet;
		}

		public Double getPellet() {
			return pellet;
		}

		/**
		 * 获取有效位数
		 * @return 有效位数
		 */
		public Integer getValid(){
			switch (this){
				case ONE_TENTH:
					return 1;
				case ONE_HUNDRED:
					return 2;
				default:
					OptimisticCoefficient.stop("计算颗粒度枚举数据无效");
					return 0;
			}
		}
	}

	public static void main(String[] args) {
		// 添加投票者和选票
		OptimisticCoefficient coefficient = new OptimisticCoefficient(
			new Voter(123L, 0.2D, 87.0D, 87.5D),
			new Voter(234L, 0.2D, 87.0D, 87.5D),
			new Voter(345L, 0.2D, 87.0D, 87.5D),
			new Voter(456L, 0.1D, 90.5D, 91.5D),
			new Voter(678L, 0.1D, 90.5D, 91.5D),
			new Voter(789L, 0.1D, 90.5D, 91.5D),
			new Voter(891L, 0.1D, 90.5D, 91.5D)
		);
		// 打开debug模式
		coefficient.openDebug();
		// 设置重合长度和细粒度, 并执行计算
		Double compute = coefficient.compute(3, Pellet.ONE_TENTH);
		LOGGER.info("本次计算的执行分数为: {}", compute);
	}
}

