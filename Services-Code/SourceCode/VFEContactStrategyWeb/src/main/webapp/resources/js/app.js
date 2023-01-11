
var $ = jQuery.noConflict();
$(function () {

	$("input[type='text'], input[role='textbox'] ,.serachTabel .ui-datatable .ui-column-filter,textarea").focus(function () {
		$(this).addClass('border-class');
		
	});
	$("input[type='text'], input[role='textbox'] ,.serachTabel .ui-datatable .ui-column-filter,textarea").blur(function () {
		$(this).removeClass('border-class');
		
	});

	  // loader  
    $('#loader').fadeOut(1500);
    
    
// bootstrap 
    $("input[type='submit'],.ui-button-text-only").addClass("btn btn-orange");
    $("input[type='text'],input[role='textbox']").addClass("form-control");
    $(".serachTabel table").first().addClass("display colored_table table-bordered table table-hover table-striped table-responsive");
    $(".ui-datatable-empty-message").addClass("alert alert-info");
  
    
    
// Accordion for navigation 
    $(".parent").parent('li').addClass("parent-li");
    $(".child").parent('li').addClass("child-li");
    $(".child-li").hide();
    $( ".parent-li" ).click(function () {
    	$( this ).children('a').toggleClass('active-pane');
    	$( this ).siblings().children().removeClass('active-pane');
    	$( this ).nextUntil( ".parent-li" ).slideToggle("fast");
        $("#wrapper").removeClass("toggled");
    	$( this ).siblings(".parent-li" ).nextUntil( ".parent-li" ).slideUp("fast");
    	
    });
    function activeSlide() {
    	$(".active-slide").parents('li').nextUntil( ".parent-li" ).show();
    	$(".active-slide").parents('li').prevUntil( ".parent-li" ).show();
    	$(".active-slide").parents('li').show();
    	$(".active-slide").parents('li').siblings( ".parent-li").children('a').removeClass('active-pane');
    	$(".active-slide").parents('li').prevAll( ".parent-li").first().children('a').addClass('active-pane');
    	
    };
    activeSlide();
    
 // scroller
	$(window).scroll(function () {
        if ($(this).scrollTop() > 800) {
            $('.scrollup').fadeIn();
        } else {
            $('.scrollup').fadeOut();
        }
    });

    $('.scrollup').click(function () {
        $("html, body").animate({
            scrollTop: 0
        }, 600);
        return false;
    });
	
    
	// addEditMultiChannel checkBoxes
    $('.addEditMultiChannel_class .ui-chkbox').parent('td').addClass('td_parent_checkbox');
	
});


function dialogHideCheck() {
	if (document.getElementById('dialog:hiddenField').value == 'true')
		PF('dlg1').hide();
	else{
		document.getElementById('dialog:hiddenField').value = 'false';
		return false;
	}
}

function displayConfirm() {
	document.getElementById("form:hiddenButton").click();

}

function scrollUp() {
    window.scrollTo(0, 0);
}

function editModeVisibility(hide, classVar, noError) {
	if (noError) {
		if (hide)
			$(classVar).hide();
		else
			$(classVar).show();
	} else
		$(classVar).hide();
}

function manageDeleteIcons(hide, classVar, noError) {
	if (noError) {
		if (hide)
			$(classVar).hide();
		else
			$(classVar).show();
	} else
		$(classVar).hide();
}

function checkDialogVisibility(classVar){
	return $(classVar).isVisible();
}

function scrollDown() {
    window.scrollTo(0, document.documentElement.scrollHeight);
}

function setTwoNumberDecimal(el) {
	if(el.value != "" && !isNaN(el.value))
		el.value=parseFloat(el.value).toFixed(2);
}

//Prevent the backspace key from navigating back.
$(document).unbind('keydown').bind('keydown', function (event) {
 var doPrevent = false;
 if (event.keyCode === 8) {
     var d = event.srcElement || event.target;
     if ((d.tagName.toUpperCase() === 'INPUT' &&
          (
              d.type.toUpperCase() === 'TEXT' ||
              d.type.toUpperCase() === 'PASSWORD' || 
              d.type.toUpperCase() === 'FILE' || 
              d.type.toUpperCase() === 'EMAIL' || 
              d.type.toUpperCase() === 'SEARCH' || 
              d.type.toUpperCase() === 'DATE' )
          ) || 
          d.tagName.toUpperCase() === 'TEXTAREA') {
         doPrevent = d.readOnly || d.disabled;
     }
     else {
         doPrevent = true;
     }
 }

 if (doPrevent) {
     event.preventDefault();
 }
});