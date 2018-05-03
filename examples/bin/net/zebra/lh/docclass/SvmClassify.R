svm.classify <- function(data)
{
	#data <- matrix(c(1, 2, 3, 4, 5, 6, 7, 8, 1, 1, 1, 1), 4, 3);

	n <- dim(data)[1];
	A <- matrix(NA, n, n);
	for(i in 1:n)
	{
		vi <- data[i, ];
		xi <- vi[1:length(vi)-1];
		yi <- vi[length(vi)];
		for(j in 1:n)
		{
			vj <- data[j, ];
			xj <- vj[1:length(vj)-1];
			yj <- vj[length(vj)];
			
			value <- yi*yj*(t(xi)%*%xj);
			A[i, j] <- value;
		}
	}
	A;
	
	#b <- rep(1, n);
	#solve(A, b);
}
