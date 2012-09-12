int foo(int n)
{
int result=0;

if (sin(n)>0)
	result = cos(n+45);
else
	result = cos(n+60);
	
return result;
}