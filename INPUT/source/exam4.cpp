int f(float a) {
	int x=5;
	float y=x/3;
	if (y==a) {
		y=y+1;
		x=3*(y+6)-8*x;
	}
	return x;
}
