
var addr='http://localhost:8080/bookDinner';
function loadOrderList(){
	var url = addr+'/order/listOrders';
	$(".sonTr").remove();
	$.get(url, function(result){
	        var arrayAll = [];
	        arrayAll = result['msg'];
	        for(var key in arrayAll){
	                $(".gridtable tbody").append($("<tr class='sonTr'><td>"+arrayAll[key]['peopleName']+"</td><td>"+arrayAll[key]['food']+"</td><td><input type='button' onclick='deleteOne(\""+arrayAll[key]['peopleName']+"\")' class='enjoy-css' value='删除' /></td></tr>"));
	        }
	  });
}


function deleteOne(name1){
        var x = confirm("确定删除？别删别人的哦~~");
        if(x){
                var url = addr+'/order/delete';
            $.post(url,{name:name1}, function(result){
                if(result['msg'] == 'succ'){
                        alert("删除成功");
                }else{
                        alert(result['msg']);
                }
                $(".gridtable").hide(100);
                loadOrderList();
                $(".gridtable").show(500);
            });
        }
}

function loadDishes(){
        var url = addr+'/order/listDishes';
        $(".sonDiv").remove();
        $.get(url, function(result){
                var arrayAll = [];
                arrayAll = result['msg'];
                for(var key in arrayAll){
                        $(".menuDiv .content").append($("<div class='sonDiv'><input  name='food' value='"+arrayAll[key]['id']+"' type='checkBox'/><span>"+arrayAll[key]['foodName']+"</span></div>"));
                }
          });


}

$(function(){
         $.ajaxSetup({cache:false});
        $(".gridtable").hide();
        loadOrderList();
        loadDishes();
        $(".gridtable").show(500);
        $("#doorder").click(function(){
                var name = $("#oneperson").val();
                if($.trim(name) == "" ){
                        alert("姓名不能为空");
                        return;
                }
                var foodIds = "";
                 $('input[name="food"]:checked').each(function(){
                           foodIds+=(","+$(this).val());
                });

                if($.trim(foodIds) == "" ){
                        alert("餐名不能不选");
                        return;
                }
                var url = addr+'/order/do';
            $.post(url,{name:$("#oneperson").val(),dishId:foodIds}, function(result){
                if(result['msg'] == 'succ'){
                        alert("订餐成功");
                }else{
                        alert(result['msg']);
                }
                $(".gridtable,.menuDiv").hide(100);
                loadOrderList();
                loadDishes();
                $(".gridtable,.menuDiv").show(500);
            });
        });
});
