int ptb3(int a, int b, int c, int d)
{
	int delta = (pow(b, 2) - ((3 * a) * c));
	if ((delta != 0))
	{
		int k = ((((((9 * a) * b) * c) - (2 * pow(b, 3))) - ((27 * pow(a, 2)) * d)) / 2);
		int temp = pow(delta, 3);
		temp = abs(temp);
		k = k / sqrt(temp);
		if ((delta > 0))
		{
			if ((abs(k) <= 1))
			{
				return 3;
			}
			else
				return 1;
		}
		else
		{
			return 1;
		}
	}
	else
		return 1;
}
