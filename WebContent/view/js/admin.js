var addr='http://localhost:8080/bookDinner';
function listOrderCount(){
	var url = addr+'/order/listOrderCount';
	$(".sonTr").remove();
	$.get(url, function(result){
	        var arrayAll = [];
	        arrayAll = result['msg'];
	        for(var key in arrayAll){
	                $(".gridtable tbody").append($("<tr class='sonTr'><td>"+arrayAll[key]['foodName']+"</td><td>"+arrayAll[key]['foodCount']+"</td></tr>"));
	        }
	  });
}

function deleteOne(name1){
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


function setEnd(endOrNot){
        var url = addr+'/order/setEndLine';
    $.post(url,{end:endOrNot}, function(result){
        if(result['msg'] == 'succ'){
                alert("设置成功");
        }else{
                alert(result['msg']);
        }
    });
}

function loadDishes(){
        var url = addr+'/order/listDishes';
        $(".sonDiv").remove();
        $.get(url, function(result){
                var arrayAll = [];
                arrayAll = result['msg'];
                for(var key in arrayAll){
                        $(".menuDiv .content").append($("<div class='sonDiv'><input  name='food' value='"+arrayAll[key]['id']+"' type='radio'/><span>"+arrayAll[key]['foodName']+"</span></div>"));
                }
          });

}

function loadEndSts(){
    var url = addr+'/order/getEndSts';
    $.get(url, function(result){
            var msg = [];
            msg = result['msg'];
     if(msg=='1'){
             $("#theEnd").val("开启订餐");
     }else{
             $("#theEnd").val("停止订餐");
     }
     $("#endStat").val(msg);
      });
}

$(function(){
         $.ajaxSetup({cache:false});
        listOrderCount();
        loadDishes();
        loadEndSts();
        $("#addDish").click(function(){
                var name = $("#oneDish").val();
                if($.trim(name) == "" ){
                        alert("餐名不能为空");
                        return;
                }

                var url = addr+'/order/addOnedish';
            $.post(url,{name:$("#oneDish").val()}, function(result){
                if(result['msg'] == 'succ'){
//                      alert("加餐成功");
                        $("#oneDish").val("")
                        loadDishes();
                }else{
                        alert(result['msg']);
                }
            });
        });
        $("#deleteDish").click(function(){
                var id1 = $('input:radio[name=food]:checked').val();
                if($.trim(id1) == "" ){
                        alert("餐名不能不选");
                        return;
                }

                var url = addr+'/order/deleteOneDish';
            $.post(url,{id:id1}, function(result){
                if(result['msg'] == 'succ'){
                        alert("删除成功");
                        loadDishes();
                        listOrderCount();
                }else{
                        alert(result['msg']);
                }
            });
        });


        $("#theEnd").click(function(){
                var endStat = $("#endStat").val();
                var endOrNot = 1;
                if(endStat==1){
                        endOrNot =0;
                }
                var url = addr+'/order/setEndLine';
            $.post(url,{end:endOrNot}, function(result){
                if(result['msg'] == 'succ'){
                        alert("设置成功");
                        $("#endStat").val(endOrNot);
                        if(endOrNot==1){
                                $("#theEnd").val("开启订餐");
                        }else{
                                $("#theEnd").val("停止订餐");
                        }

                }else{
                        alert(result['msg']);
                }
            });
        });

});

