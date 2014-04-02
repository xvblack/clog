		var username;
		function showSideBar()
		{
			$("#sidebar").css("display","inline-block");
			$("#onlyButton").css("display","none");
  			$("#wrapper").css({
  				"width": "60%",
					"padding": "0px 8% 0px 8%"
  			});
		}
		function hideSideBar()
		{
			$("#sidebar").css("display","none");
			$("#onlyButton").css("display","inline-block");
  			$("#wrapper").css({
  				"width": "94%",
				"padding": "0px 3% 0px 3%"
  			});
		}
		function showRespSidebar()
		{
			$("#sidebar").css({
  				"display":"inline-block",
  				"position":"fixed",
  				"top":"0",
  				"z-index":"1",
  				"width":"90%",
  				"padding":"0 5% 0 5%",
  				"height":"100%"
  			});
  			$("#userActions").append("<p id='menuHide'><a href='javascript:hideRespSidebar()'> Back to contents</a></p>");
  		
		}
		function hideRespSidebar()
		{
			viewportWidth  = $(window).width();
			if(viewportWidth>=640)
			{
				$("#sidebar").css({
	  				"position":"fixed",
	  				"top":"0",
	  				"width":"20.5%",
	  				"padding":"0px 1.75% 0 1.75%",
	  				"height":"100%",
	  				"right" : "0",
	  				"overflow-y" : "auto"
	  			});
			}
			else
			{
				hideSideBar();
			}
  			$("#menuHide").remove();

		}



		$(document).ready(function(e){
			username=$("#userActions .username").html();
			var viewportWidth  = $(window).width();
  			var viewportHeight = $(window).height();
  			//lert(viewportHeight);
  			//alert(viewportWidth);
  			// if viewport< someValue, hide the sidebar
  			// reset the CSS
  			// somehow like manual responsive.
  			// That is beacuse I have no time.
  			if(viewportWidth<=640)
  			{
  				hideSideBar();
  			}
  			if(viewportWidth*0.6>1200)
  			{
  				// calculate the newsize of wrapper
  				var newPadding = ((viewportWidth * 0.8) - 1200) / 2;
  				$("#wrapper").css({
		  						"padding-left": newPadding,
		  						"padding-right": newPadding
		  					});
  			}
  			// we also create a listener to automatically do this.
  			// maybe refactorated to a function.
  			$( window ).resize(function() {
			  	viewportWidth  = $(window).width();
  				viewportHeight = $(window).height();
  				if(viewportWidth<=640)
	  			{
	  				hideRespSidebar(username);
	  				hideSideBar();
	  			}
	  			else
	  			{
	  				if(viewportWidth*0.6>1200)
		  			{
		  				// calculate the newsize of wrapper
	  					hideRespSidebar(username);
	  					showSideBar();
		  				var newPadding = ((viewportWidth * 0.8) - 1200) / 2;
		  				$("#wrapper").css({
		  						"padding-left": newPadding,
		  						"padding-right": newPadding
		  					});
	  				}
	  				else
	  				{
	  					hideRespSidebar(username);
	  					showSideBar();

	  				}
	  				
	  			}
			});

			$("#onlyButton").click(function(){
				//check screen resolution
				hideRespSidebar(username);
				showRespSidebar();
			})
			// visul marker for editable contents
			$("[contenteditable='true']").css("border-bottom","1px solid #78b437");
		});
