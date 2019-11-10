# Poker Bot
This bot was able to play poker autonomously at four tables simultaneously on a major poker website.

## Strategy
In the beginning, used Java to implement the core concept of a paper of the Computer Poker Research Group at UA Alberta published at AAAI 2012 [https://poker.cs.ualberta.ca/publications/AAMAS12-pcs.pdf] to approximate Nash equilibrium by Monte Carlo Counterfactual Regret Minimization. Then, implemented again the algorithm in C to achieve a higher accurarcy.

Implemented concepts of paper from Knowledge Engineering Group at TU Darmstadt published at KI 2009 to adopt to behavior of opponents via Monte Carlo search [https://doi.org/10.1007/978-3-642-04617-9_9].
