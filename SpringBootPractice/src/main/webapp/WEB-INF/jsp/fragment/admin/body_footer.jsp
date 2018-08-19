<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/taglib.jspf" %>

        <!-- Navigation -->
		<footer class="sticky-footer">
      		<div class="container" style="margin-top: 20px; margin-bottom: 20px;">
       			<div class="text-left">
          			<small>COPYRIGHT © {} ALLRIGH RESERVED.</small>
       			</div>
      		</div>
    	</footer>
    
	    <!-- Logout Modal-->
		<div class="modal fade" id="logoutModal" tabindex="-1" role="dialog" aria-labelledby="logoutModalLabel" aria-hidden="true">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
	          		<div class="modal-header">
	            		<h4 class="modal-title" id="logoutModalLabel">로그아웃</h4>
	          		</div>
	          		<div class="modal-body">
	          			<span class="icon">
		          			<i class="fa fa-question-circle fa-3" aria-hidden="true"></i>
	          			</span>
	          			<div class="message">로그아웃 하시겠습니까?</div>
	          		</div>
	          		<div class="modal-footer">
	          			<%-- Spring Security 4 이후, Secrity Logout은 POST만 가능 --%>
	            		<form:form action="${pageContext.request.contextPath}/admin_logout" method="post">
		            		<button class="btn btn-secondary" type="button" data-dismiss="modal">취소</button>
	            			<button class="btn btn-primary" type="submit">로그아웃</button>
	            		</form:form>
	          		</div>
	        	</div>
	      	</div>
		</div>
		
		<!-- Alert Modal-->
		<div class="modal fade" id="alertModal" tabindex="-1" role="dialog" aria-labelledby="alertModalLabel" aria-hidden="true">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
	          		<div class="modal-header">
	            		<h4 class="modal-title" id="alertModalLabel">알림</h4>
	          		</div>
	          		<div class="modal-body">
          				<span class="icon">
          					<i class="fa fa-exclamation-triangle fa-3" aria-hidden="true"></i>
          				</span>
          				<div class="message"></div>
	          		</div>
	          		<div class="modal-footer">
	            		<button class="btn btn-secondary" type="button" data-dismiss="modal">확인</button>
	          		</div>
	        	</div>
	      	</div>
		</div>
		
	    <!-- Confirm Modal-->
		<div class="modal fade" id="confirmModal" tabindex="-1" role="dialog" aria-labelledby="confirmModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
	          		<div class="modal-header">
	            		<h4 class="modal-title" id="confirmModalLabel">확인</h4>
	          		</div>
	          		<div class="modal-body">
	          			<span class="icon">
		          			<i class="fa fa-question-circle fa-3" aria-hidden="true"></i>
	          			</span>
	          			<div class="message"></div>
	          		</div>
	          		<div class="modal-footer">
	            		<button class="btn btn-secondary" type="button" data-dismiss="modal">취소</button>
	            		<button class="btn btn-primary" type="button" name="ok">확인</button>
	          		</div>
	        	</div>
	      	</div>
		</div>
		