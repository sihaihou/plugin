function sleep(numberMillis) {
    var now = new Date();
    var exitTime = now.getTime() + numberMillis;
    while (true) {
        now = new Date();
        if (now.getTime() > exitTime){
            return;
        }
    }
}
/**
 * 获取JSon对象的长度
 * @param jsonData
 * @returns
 */
function getJSonObjLenth(jsonObj){
	var jsonLength=0;
	for(var item in jsonObj){
		jsonLength++;
	}
	return jsonLength;
}


