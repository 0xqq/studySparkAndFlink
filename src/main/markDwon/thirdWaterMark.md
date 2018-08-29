
## 三、waterMark

[下面引述：Flink流计算编程--watermark（水位线）简介，如需了解更多相关，请点击](https://blog.csdn.net/lmalds/article/details/52704170)

[avatar](src/main/images/waterMark.png) 

- 图中蓝色虚线和实线代表着watermark的时间。

###注意点

值得关注的是，该引述的watermark中存在的是个keyby，不会出现多个watermask，
而在本demo样例里面我们需要关注到的点是这个我们默认的四个并行度，做出同等的
操作的时候无法为能够看到博客中引述的问题，但是原理一样，需要进行keyby的hash,
所以在这里就必须给对应的并行度进行给定的hash值，亦能够看到到博客引述的问题和结果

###waterMarkDemo的位置

[avatar](waterMarkDemo/WatermarkTest.scala) 