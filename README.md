# 課題２ 物件価格予測回帰問題

## データセットの作成

### 目的変数

[Price.csv](https://github.com/ia15076/internship-coding-tasks/blob/price/src/main/resources/Price.csv)のpriceカラムをそのまま目的変数として利用した。

### 説明変数

[Price.csv](https://github.com/ia15076/internship-coding-tasks/blob/price/src/main/resources/Price.csv)に含まれるカラムから、目的変数に利用したpriceカラムと、連続値ではないようだったwaterfrontカラム(海辺かどうか?)とzipカラム(郵便番号)を除いて、残った他の全てのカラムを説明変数に利用した。

いくつかのカラムについては下表に示すような前処理を施した。

|カラム|前処理|
|---|---|
|date(販売日時)|元は文字列なのを連続的な値とするため、日時としてパースしてからエポック秒に変換する前処理を行った。|
|yr_renovated(リノベーションされた年)|一度もリノベーションされていないと思われる物件が「0」で無効な値となっているのは適切でないと思ったので、「0」の場合はyr_builtカラム(建築された年)の値で置き換える前処理を行った。|

さらに、今回の課題要件ではPrice.csvをデータとして利用せよとあったが、他のデータを利用してはならないとも書かれていなかったから、[US Zip Codes Database](https://simplemaps.com/data/us-zips)にあった郵便番号データを利用して、次のような説明変数を追加した。但しモデルの評価については郵便番号データを利用する場合としない場合で別々に行った。

|カラム|
|---|
|population(人口)|
|density(人口密度)|

これらの前処理が終わった後、全ての説明変数について[-1.0,1.0]の範囲でスケーリングを行った。

### その他

後のモデル構築のため、データセットはLIBSVM形式で保存した。(参考: [input.scale](https://github.com/ia15076/internship-coding-tasks/blob/price/src/main/resources/input.scale))

トレーニングセットとテストセットの分割については、データセットの各行をランダムに並び替えてから3つに分割し、2つをトレーニングセット、1つをテストセットにしてモデルの評価を行うようにした(3分割交差検証)。

## モデル構築

回帰問題ではSVRが安定して良い成績を発揮するという経験則から、[LIBSVM](https://www.csie.ntu.edu.tw/~cjlin/libsvm/)を利用してモデルの構築を行った。

パラメータは以下のように設定した。

|パラメータ|値|
|---|---|
|-s(svm_type)|3(epsilon-SVR)|
|-v(n-fold cross validation mode)|3|
|-c(cost)|33554432|
|-g(gamma)|0.0625|
|-p(epsilon)|0.0009765625|

パラメータのうち-c・-g・-pについては平均二乗誤差を最小化するようにグリッドサーチを行って最適値を得ようとしたものであるが、-cについては探索を行った範囲の最大値(2^25)が最適解となってしまった。
より広い範囲でグリッドサーチをやり直しているが、-cが大きくなるほど学習にかかる時間は増えるので、10/3に始めたグリッドサーチが現在(10/5)になっても未だに終わっていない。したがって今回のところは先のパラメータ設定を採用することにした。

なお処理時間の都合から、グリッドサーチにはLIBSVMではなく、より高速な互換ソフトである[ThunderSVM](https://thundersvm.readthedocs.io/en/latest/)を利用している。

表に書かなかったパラメータについてはLIBSVMのデフォルト値を利用した。

## テスト結果

LIBSVMはMAEを計算してくれないので、計算するように小改造を行っている。

### 郵便番号データを利用した場合

![郵便番号データを利用した場合の結果](https://user-images.githubusercontent.com/19220989/66256424-a0471800-e7c8-11e9-98db-82b0402fec6e.png)

|指標|値|
|----|----|
|MAE|105262.92414249941|
|RMSE|176928.077551|
|R2スコア|0.771901388114052|

### 郵便番号データを利用しなかった場合

![郵便番号データを利用しなかった場合の結果](https://user-images.githubusercontent.com/19220989/66256030-df736a00-e7c4-11e9-8a96-b6fed6fed8a3.png)

|指標|値|
|----|----|
|MAE|120616.36878997303|
|RMSE|190727.790643|
|R2スコア|0.7338010705662587|

## コード実行手順

動作環境：Java(>=8), Maven 3

1. このブランチをclone

```
git clone -b price https://github.com/ia15076/internship-coding-tasks.git
```

2. コンパイル

```
cd internship-coding-tasks
mvn compile
```

3. 実行

```
mvn exec:java
```
