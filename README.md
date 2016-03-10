# AdaBoosting-MachineLearning

Adaptive Boosting is a framework that shows us how to:
- Generate multiple weak hypothesis from a given weak learning algorithm.
- Compute more powerful algorithm from these hypothesis

There are many extensions of AdaBoosting. Through this project we tried to implement Binary AdaBoosting(Standard) and Real AdaBoosting.

Input : The training data, given as N pairs(x,y) where x is attributes vector, y is desired output, either +1 or -1. The number of iterations T.

Output : A function f(x) that can be used to classify the attribute vector x. If f(x) < 0 classify x as -1. If f(t) > 0, classify x as 1.


Input file format:
------------------
10 4 0.0000001

1 2 3.5 4.5

1 -1 1 1

0.25 0.25 0.25 0.25

The first number of the first row indicates the number of iterations. The next number is the total number of training data items.
The second and third rows indicate the training data as pairs (x,y) where x is attributes vector, y is desired output.
Fourth row is the initial probability.

Binary AdaBoosting :
====================
Output:
-------
Iteration:1

* The selected weak classifier: x > 2.75
* The error of Ht:0.25
* The weight of Ht:0.5493
* The probabilities normalization factor Zt:0.866
* The probabilities after normalization:	0.5	0.1667	0.1667	0.1667
* The boosted classifier: 0.5493*I(x>2.75)
* The error of the boosted classifier Et:0.25
* The bound on Et:0.866

Real AdaBoosting:
=================

Output:
-------
Iteration:1

* The selected weak classifier: x < 1.3792905807495117
* The G error value of Ht:0.4525
* The weights Ct+, Ct-:0.3466,-6.4496
* The probabilities normalization factor Zt:0.9051
* The probabilities after normalization:	0.0313	0.0625	0.0313	0.0313	0.0313	0.0625	0.0625	0.0313	0.0313	0.0313	0.0313	0.0313	0.0625	0.0313	0.0313	0.0625	0.0313	0.0625	0.0625	0.0313	0.0625	0.0313	0.0313	0.0313	1.0E-4
* The values ft(xi) for each one of the examples:	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	0.3466	-6.4496
* The bound on Et:0.9051

These were carried out for 10 iterations.

Reference:
----------
https://en.wikipedia.org/wiki/AdaBoost
