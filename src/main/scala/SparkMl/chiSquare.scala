package SparkMl

import sparkSql.getSpark
import org.apache.spark.ml.feature.{ChiSqSelector, ChiSqSelectorModel}
import org.apache.spark.ml.linalg.Vectors


//特征选取–卡方选择器

object chiSquare {
  def main(args: Array[String]): Unit = {
    val spark = new getSpark().getSparkSession("chiSquare","local[2]")

    // 创造实验数据，这是一个具有三个样本，四个特征维度的数据集，标签有1，0两种，我们将在此数据集上进行卡方选择：
    val df = spark.createDataFrame(Seq(
       (1, Vectors.dense(0.0, 0.0, 18.0, 1.0), 1),
       (2, Vectors.dense(0.0, 1.0, 12.0, 0.0), 0),
       (3, Vectors.dense(1.0, 0.0, 15.0, 0.1), 0)
       )).toDF("id", "features", "label")
    // 现在，用卡方选择进行特征选择器的训练，为了观察地更明显，我们设置只选择和标签关联性最强的一个特征（可以通过setNumTopFeatures(..)方法进行设置）
    val selector = new ChiSqSelector().
      setNumTopFeatures(1).
      setFeaturesCol("features").
      setLabelCol("label").
      setOutputCol("selected-feature")

//用训练出的模型对原数据集进行处理，可以看见，第三列特征被选出作为最有用的特征列：
    val selector_model = selector.fit(df)

    val result = selector_model.transform(df)

    result.show(false)

  }
}
