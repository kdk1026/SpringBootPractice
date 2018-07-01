<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/taglib.jspf" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>Insert title here</title>
<style>  
.mytable { border-collapse:collapse; }  
.mytable th, .mytable td { border:1px solid black; }
.mytable td:nth-child(1) {width: 10px; text-align: center;}
.mytable td:nth-child(2) {width: 400px;}
.mytable td:nth-child(3) {width: 100px; text-align: center;}
</style>
</head>
<body>
	<script src="${pageContext.request.contextPath}/webjars/jquery/3.2.1/dist/jquery.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/commonJquery.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/fileUtils.js"></script>

	<div><a href="${contextPath}/pdf/pdf_down">PDF 다운</a></div>
	
	<div style="margin-top: 15px;">
		<form:form name="frm" action="${contextPath}/pdf/pdf_upload?${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
			<input type="file" name="file" required />
			<input type="submit" value="전송" />
		</form:form>
	</div>
	
	<div style="margin-bottom: 15px; color: red;">${resp.resp_msg}</div>
		
	<div>
		${item}
	</div>
	
	<script type="text/javascript">
	var $frm = $('form[name="frm"]');
	var $file = $frm.find('input[name="file"]');
	
	$file.change(function(){
		var fileObj = FileUtils.getFileInfo($file[0]);
		var sFileExt = FileUtils.getFileExtension(fileObj);
		
		var isAllowed = true;
		var excelExtensions = ['pdf'];
		
		if ( !excelExtensions.contains(sFileExt) ) {
			isAllowed = false;
			alert('<spring:eval expression="@file['file.is.not.type.msg']" />');
		}
		
		if (isAllowed) {
			var nLimitSize = '<spring:eval expression="@file['file.limit.size']" />';
			if ( fileObj.size > nLimitSize ) {
				isAllowed = false;
				alert('<spring:eval expression="@file['file.limit.size.msg']" />');
			}			
		}
		
		if (!isAllowed) {
			var isMSIE = $.IEVersionCheck();
			if ( isMSIE ) {
				$file.replaceWith( $file.clone(true) );
			} else {
				$file.val('');
			}
		}
	});
	
	$frm.submit(function() {
		$(':required', this).parent().show();
		
		var invalidInputs = $(":invalid", this);
		if(invalidInputs.length > 0) {
			return false;
		}
		
		if(event.originalEvent) {
			return false;
		}
	});
	</script>
	
</body>
</html>