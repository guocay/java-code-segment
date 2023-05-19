# 捕鱼达人(Java版)

接口(interface)
1) 相当于一种抽象类
2) 属性只能是常量, 方法只能是抽象方法的
3) 语法接近于抽象类
4) 子类实现(implement)接口(就是子类继承接口)
5) 接口可以实现"多继承"的逻辑关系

能跑的 Runner  maxSpeed()
猎手 Hunter    hunt()
猫 Cat

猫是猎手也能跑

业务对象关系模型

pool（鱼池）
|-- 多个 fish(鱼)
|-- 一个 net (渔网)

数据模型：如何将业务对象映射到数据
参考绘图坐标系

类的设计

```
com.github.guocay.game.Pool 继承  JPanel
|-- background 背景图片
|-- com.github.guocay.game.Fish[] allFish
|-- com.github.guocay.game.Net net

com.github.guocay.game.Fish
|-- x,y
|-- width
|-- height
|-- images 鱼的动画帧图片
|-- index 图片序号
|-- image 鱼的当前图片

com.github.guocay.game.Net
|-- x,y 中心位置
|-- width
|-- height
|-- image
```

构造器设计（数据的初始化）
如何初始化数据模型

设计功能：将软件的功能映射到数据计算（算法）
游动
抓捕


算法策略：
1) 对象是数据的打包
2) 功能是利用算法修改数据实现
   鱼的游动：更改鱼x坐标和鱼的图片实现 
3) 定时重绘界面， 在重绘时候采用算法更新以后的数据。
   界面就显示出运动的效果


Debug：打虫子！  bug：虫子

Debug的前提是： 学会什么是正确！
