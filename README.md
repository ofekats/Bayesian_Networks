# Bayesian_Networks

## Overview
This project implements two key algorithms used in Bayesian Networks:
1. **Bayes Ball**: A graphical algorithm to determine conditional independence.
2. **Variable Elimination**: A method for efficient probabilistic inference in Bayesian networks.

## Algorithms

### 1. **Bayes Ball Algorithm**
The Bayes Ball algorithm is used to determine whether two variables in a Bayesian Network are conditionally independent, given a set of observed variables. It's a fast, graphical method that traverses the network to check for paths of influence between variables.

#### Key Features:
- Efficiently determines whether changes in one variable affect another variable, given a set of observations.
- The algorithm works by "throwing" a metaphorical ball across the network. Depending on the structure of the network and the observed nodes, the ball either passes through or gets blocked.
  
#### Use Case:
- Conditional independence queries are essential for optimizing inference in Bayesian networks and are critical for simplifying complex probability distributions.

### 2. **Variable Elimination Algorithm**
Variable Elimination is a fundamental inference algorithm for Bayesian networks. It computes marginal probabilities by systematically summing out irrelevant variables (variables not needed for the query), reducing the computational complexity.

#### Key Steps:
1. Identify the variables to be eliminated.
2. Multiply the factors that involve the variable to be eliminated.
3. Sum out the variable from the resulting product.
4. Continue eliminating variables until the desired probability is computed.

#### Use Case:
- It's a powerful tool for exact inference in probabilistic graphical models, useful in applications such as diagnostics, decision making, and machine learning.

## How to Run

To compile and run the algorithms:

```bash
   javac *.java
   java Ex1
```
