<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sadi" uri="/WEB-INF/sadi.tld" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="ca.wilkinsonlab.sadi.common.SADIException" %>
<%@ page import="ca.wilkinsonlab.sadi.registry.*" %>
<%
	Logger log = Logger.getLogger("ca.wilkinsonlab.sadi.registry");
	Registry registry = Registry.getRegistry();
	
	String serviceURI = request.getParameter("serviceURI");
	if (serviceURI != null) {
		try {
			ServiceBean service = ServiceValidator.validateService(serviceURI);
			pageContext.setAttribute("service", service);
		} catch (final SADIException e) {
			ServiceBean service = new ServiceBean();
			service.setServiceURI(serviceURI);
			request.setAttribute("service", service);
			request.setAttribute("error", e.getMessage());
			doGet(request, response);
		}
	}
%>
<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
 "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>SADI registry &mdash; validate a service</title>
    <link rel="icon" href="/favicon.ico" type="image/x-icon">
    <link rel="stylesheet" type="text/css" href="../style/sadi.css">
  </head>
  <body>
    <div id='outer-frame'>
      <div id='inner-frame'>
        <div id='header'>
          <h1><a href="http://sadiframework.org/">SADI</a></h1>
          <p class='tagline'>Find. Integrate. Analyze.</p>
        </div>
        <div id='nav'>
          <ul>
            <li class="page_item current_page_item"><a href="../validate">Validate</a></li>
            <li class="page_item"><a href="../register">Register</a></li>
            <li class="page_item"><a href="../services">Services</a></li>
            <li class="page_item"><a href="../sparql">SPARQL</a></li>
          </ul>
        </div>
        <div id='content'>
          <h2>Validate a service</h2>
          
       <c:if test='${service != null}'>
	    <c:choose>
	     <c:when test='${error != null}'>
	      <div id='registration-error'>
	        <h3>Error</h3>
	        <p>There was an error validating the service at <a href='${service.serviceURI}'>${service.serviceURI}</a>:</p>
	        <blockquote>${error}</blockquote>
	      </div>
	     </c:when>
	     <c:otherwise>
	       <div id='registration-success'>
	         <h3>Success</h3>
	         <p>Successfully validated the service at <a href='${service.serviceURI}'>${service.serviceURI}</a>.</p>
	         <!-- include service.jsp -->
	       </div>
	     </c:otherwise>
	    </c:choose>
	   </c:if>

      <div id='registration-form'>
        <form method='POST' action='.'>
          <label id='url-label' for='url-input'>Enter the URL of the service you want to validate...</label>
          <input id='url-input' type='text' name='url' value='<c:if test='${error != null}'>${registered.serviceURI}</c:if>'>
          <input id='register-submit' type='submit' value='...and click here to validate it'>
        </form>
      </div> <!-- registration-form -->
      
        </div> <!-- content -->
        <div id='footer'>
          <img class="sponsor" style="margin-top: 10px;" src="../images/HSFBCY.gif" alt="HSFBCY logo" height="62" width="134"/>
          <img class="sponsor" style="margin-top: 16px;" src="../images/CIHR.png" alt="CIHR logo" height="62" width="91"/>
          <p>Development of SADI is generously supported by 
            <span class="nobreak">the Heart and Stroke Foundation of B.C. and Yukon</span>,
            <span class="nobreak">the Canadian Institutes of Health Research</span>, and 
            <span class="nobreak">Microsoft Research</span>.
          </p>
        </div> <!-- footer -->
      </div> <!-- inner-frame -->
    </div> <!-- outer-frame -->
  </body>
</html>
