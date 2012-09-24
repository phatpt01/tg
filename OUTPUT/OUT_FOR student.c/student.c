int foo(int N, int M)
{
	int a;
	int b;
	int c;
	int d;
	int e;
	int i = 1;
	int gt = 1;
	while ((i <= N))
	{
		(gt *= i);
		i = i + 1;
	}
	return gt;
}
