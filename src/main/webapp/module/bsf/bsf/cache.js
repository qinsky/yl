ul.cache = {};
  
ul.cache.add = function(key,data)
{
	if(!sessionStorage){
		return;
	} 
	sessionStorage.setItem(key,data);
};

ul.cache.get = function(key)
{  
	if(!sessionStorage){
		return;
	}
	var cacheData =  sessionStorage.getItem(key);
	if(cacheData)
	{
		console.log("Congratulation hit cache!");
	}
	return cacheData;
}; 