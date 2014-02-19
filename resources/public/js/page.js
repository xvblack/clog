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
  				"width": "80%",
				"padding": "0px 10% 0px 10%"
  			});
		}
		$(document).ready(function(e){
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
	  				hideSideBar();
	  			}
	  			else
	  			{
	  				if(viewportWidth*0.6>1200)
		  			{
		  				// calculate the newsize of wrapper
		  				var newPadding = ((viewportWidth * 0.8) - 1200) / 2;
		  				$("#wrapper").css({
		  						"padding-left": newPadding,
		  						"padding-right": newPadding
		  					});
	  				}
	  				else
	  				{
	  					showSideBar();
	  				}
	  				
	  			}
			});
		});
