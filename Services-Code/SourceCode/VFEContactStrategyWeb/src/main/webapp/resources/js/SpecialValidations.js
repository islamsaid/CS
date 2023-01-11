var isIE = document.all ? true : false;
//hazem

var isIE11 = navigator.userAgent.match(/Trident.*rv[ :]*11\./) ? true : false;
var isNS = document.layers ? true : false;
var regForEng = /^[A-Za-z0-9_ \\/*&^%$#@.]$/;
var tabKey = 9;//Mina
var backspaceKey = 8;//Mina
var shiftKey = 16;//Mina
var ctrlKey = 17;//Mina
var spaceKey = 32; //Mina
var leftArrow = 37;
var upArrow = 38;
var rightArrow = 39;
var downArrow = 40;
//Mina
var isShiftKeyDown = false;

//Hazem
function disableEnterSubmit() {
    if (event.keyCode === 13) {
        return false;
    }
}

function onlyNumbers(E)
{
    onKeyDown();
    if (isIE) {
        if (window.event.keyCode < 48 || window.event.keyCode > 57)
        {
//            alert(window.event.keyCode);
            if (window.event.keyCode !== backspaceKey && window.event.keyCode !== tabKey
                    && window.event.keyCode !== leftArrow && window.event.keyCode !== rightArrow) {
                if (window.event.keyCode < 96 || window.event.keyCode > 105) {
                    window.event.keyCode = 0;
                    return false;
                }
            }
        } else {
            if (isShiftKeyDown) {
                return true;
            }
        }
    }
    return true;
}
//Hazem
function isNumber(e) {
    onKeyDown();
    if (isIE || isIE11) {
        if (window.event.keyCode !== 46 && window.event.keyCode > 31 &&
                (window.event.keyCode < 48 || window.event.keyCode > 57) &&
                (window.event.keyCode < 96 || window.event.keyCode > 105)) {
            window.event.keyCode = 0;
            return false;
        } else {
            return true;
        }
    }
}
//Mina
function noSpecialChars(e) {
    var k;
    k = window.event.keyCode;
    onKeyDown();
    return (((k > 64 && k < 91) || (k > 96 && k < 123) || k === 8 || k === 32 || (k >= 48 && k <= 57 && !isShiftKeyDown) || k === ctrlKey));
}

//Amany
function noSpecialCharsWithScrolling(e) {
    var k;
    k = window.event.keyCode;
    onKeyDown();
    return (((k > 64 && k < 91) || (k > 95 && k < 123) || k === 9 || (k > 36 && k < 41) || k === 8 || k === 32 || (k >= 48 && k <= 57 && !isShiftKeyDown) || k === ctrlKey));
}

function noSpace(e) {
    var k;
    k = window.event.keyCode;
    onKeyDown();
    return (k !== spaceKey);
}

//Mina
function noSpecialCharsNoSpaces(e) {
    var k;
    k = window.event.keyCode;
    onKeyDown();
    if (k === spaceKey) {
        return false;
    }
    return (((k > 64 && k < 91) || (k > 96 && k < 123) || k === 8 || k === 32 || (k >= 48 && k <= 57 && !isShiftKeyDown) || k === ctrlKey || k === tabKey));
}

function noArabicNoSpecialChars(e) {
    noArabic(e);
    return (noSpecialChars(e));
}

//function noArabicNoSpecialCharsNoSpaces(e) {
//    noArabic(e);
//    return (noSpecialCharsNoSpaces(e));
//}
//Mina
function backSpaceEraseAll(e) {
    var k;
    k = window.event.keyCode;
    onKeyDown();
    if (k === backspaceKey) {
        window.event.srcElement.value = "";
    }
    return false;
}
//Mina
function validateLength(maxLength) {
    var srcLength;
    srcLength = window.event.srcElement.value.length;
    key = window.event.keyCode;
    if (srcLength >= maxLength && key !== tabKey && key !== backspaceKey && key !== leftArrow && key !== rightArrow) {
        return false;
    }
    return true;
}

//Mina
function noArabic(e) {
    var text = window.event.srcElement.value;

    if (text.length !== 0) {
        var newText = preventArabic(text);
        window.event.srcElement.value = newText;
    }
}

//Amanyy (El zatona ll no arabic  added 24/6)
function isEnglishchar(e) {
    var allowedText = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890._-*&^%$#@!'/\\{}()[]<>?:;~, |";
    var str = window.event.srcElement.value;
    var result_str = "";
    // var str_length = str.length;
    var Char_At = "";
    var somthChanged = false;
    k = window.event.keyCode;
    try {
        for (i = 0; i < str.length; i++) {
            Char_At = str.charAt(i);
            if (allowedText.indexOf(Char_At, 0) === -1 && k !== leftArrow && k !== rightArrow && k !== upArrow && k !== downArrow && k !== backspaceKey && k !== shiftKey && k !== spaceKey) {
                somthChanged = true;
            } else {
                result_str = result_str + Char_At;
            }
        }
        if (somthChanged) {
            window.event.srcElement.value = result_str;
        }
    }
    catch (err) {
        alert(err);
        window.event.srcElement.value = "";
    }
    return true;
}
function isEnglishcharWithDot_(e) {
    var allowedText = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890._";
    var str = window.event.srcElement.value;
    var result_str = "";
    // var str_length = str.length;
    var Char_At = "";
    var somthChanged = false;
    k = window.event.keyCode;
    try {
        for (i = 0; i < str.length; i++) {
            Char_At = str.charAt(i);
            if (allowedText.indexOf(Char_At, 0) === -1 && k !== leftArrow && k !== rightArrow && k !== upArrow && k !== downArrow && k !== backspaceKey && k !== shiftKey && k !== spaceKey) {
                somthChanged = true;
            } else {
                result_str = result_str + Char_At;
            }
        }
        if (somthChanged) {
            window.event.srcElement.value = result_str;
        }
    }
    catch (err) {
        alert(err);
        window.event.srcElement.value = "";
    }
    return true;
}
function isEnglishcharWithoutSpecialCharacters(e) {
    var allowedText = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    var str = window.event.srcElement.value;
    var result_str = "";
    // var str_length = str.length;
    var Char_At = "";
    var somthChanged = false;
    k = window.event.keyCode;
    try {
        for (i = 0; i < str.length; i++) {
            Char_At = str.charAt(i);
            if (allowedText.indexOf(Char_At, 0) === -1 && k !== leftArrow && k !== rightArrow && k !== upArrow && k !== downArrow && k !== backspaceKey && k !== shiftKey && k !== spaceKey) {
                somthChanged = true;
            } else {
                result_str = result_str + Char_At;
            }
        }
        if (somthChanged) {
            window.event.srcElement.value = result_str;
        }
    }
    catch (err) {
        alert(err);
        window.event.srcElement.value = "";
    }
    return true;
}





//Amanyy (El zatona ll no arabic no space no special chars added 25/6 )
function noArabicNoSpaceNoSpecialChars(e) {
    var orgi_text = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    var str = window.event.srcElement.value;
    var result_str = "";
    // var str_length = str.length;
    var Char_At = "";
    k = window.event.keyCode;
    try {
        for (i = 0; i < str.length; i++) {
            Char_At = str.charAt(i);
            if (orgi_text.indexOf(Char_At, 0) === -1 && k !== backspaceKey && k !== shiftKey) {

            }
            else
            {
                result_str = result_str + Char_At;
            }
        }
        window.event.srcElement.value = result_str;
    }
    catch (err) {
        alert(err);
        window.event.srcElement.value = "";
    }
    return true;
}


/*Update 2595 */
function noArabicNoApostropheNoQuotationMark(e) {
    var str = window.event.srcElement.value;
    var result_str = "";
    // var str_length = str.length;
    var Char_At = "";
    k = window.event.keyCode;
    try {
        if ((str.indexOf("'") === -1) && (str.indexOf('"') === -1)){
            result_str = str.substring(0, str.length);
        }
        else {
            result_str = str.substring(0, str.length-1);
        }
        window.event.srcElement.value = result_str;
    }
    catch (err) {
        alert(err);
        window.event.srcElement.value = "";
    }
    return true;
}


//Amanyy (El zatona ll no arabic no special chars added 25/6 )
function noArabicNoSpecialChars_(e) {
    var orgi_text = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 ";
    var str = window.event.srcElement.value;
    var result_str = "";
    // var str_length = str.length;
    var Char_At = "";
    k = window.event.keyCode;
    try {
        for (i = 0; i < str.length; i++) {
            Char_At = str.charAt(i);
            if (orgi_text.indexOf(Char_At, 0) === -1 && k !== backspaceKey && k !== shiftKey) {

            }
            else
            {
                result_str = result_str + Char_At;
            }
        }
        window.event.srcElement.value = result_str;
    }
    catch (err) {
        alert(err);
        window.event.srcElement.value = "";
    }
    return true;
}
//Mina el brince
function preventArabic(input) {
    var orgi_text = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890._-*&^%$#@!'/\\;~";

    var Char_At = "";

    if (input !== "" && input !== null)
    {
        var str_length = input.length;
//        alert('here');
        try {
            var cInput = new Array();
            for (var i = 0; i < input.length; i++)
            {
                cInput[i] = input.charCodeAt(i);
                // alert(cInput[i]);
                if (parseInt(cInput[i]) >= 1568 && parseInt(cInput[i]) <= 1620) {
                    input = input.substring(0, i) + input.substring(i + 1, input.length);
                }
            }

        }

        catch (err) {
            //     alert(err);
            input = "";
        }
        return input;
    }
}
//Mina
function onlyNumWithMaxLength(maxLength) {
    var onlyNum = onlyNumbers();
    var maxLength = validateLength(maxLength);

    return(onlyNum && maxLength);
}

function disableRightClick(event)
{
    if (event.button === 2)
    {
        event.preventDefault();
        event.stopPropagation();
        event.stopped = true;
        return false;
    }
    return true;
}
//function noSpecialChars(input, acceptedChar)
//{
//    var bolReturn = true;
//    if (input != "")
//    {
//        var cInput = new Array();
//        for (var i = 0; i < input.length; i++)
//        {
//            cInput[i] = input.charCodeAt(i);
//            //updated by nada nagy to accept single quote
//            if ((parseInt(cInput[i]) >= 33 && parseInt(cInput[i]) <= 47 && parseInt(cInput[i]) != 45 && parseInt(cInput[i]) != 46) || (parseInt(cInput[i]) >= 58 && parseInt(cInput[i]) <= 64) || (parseInt(cInput[i]) >= 91 && parseInt(cInput[i]) <= 96 && parseInt(cInput[i]) != 95) || (parseInt(cInput[i]) >= 123 && parseInt(cInput[i]) <= 126)) {
//                bolReturn = false;
//            }
//            if (acceptedChar != null && acceptedChar != "" && parseInt(cInput[i]) == acceptedChar) {
//                bolReturn = true;
//            }
//        }
//    }
//    return bolReturn;
//}

function onKeyUp(e) {
    if (window.event.keyCode === shiftKey) {
        isShiftKeyDown = false;
    }
//    preventArabic();
}

function onKeyDown() {
    if (window.event.keyCode === shiftKey) {
        isShiftKeyDown = true;
    }
}
function onlyDecimalNumbers(E)
{
    var _ret = true;
    if (isIE)
    {
        if (window.event.keyCode != 46 && (window.event.keyCode < 48 || window.event.keyCode > 57))
        {
            window.event.keyCode = 0;
            _ret = false;
        }
    }
    if (isNS)
    {
        if (window.event.keyCode != 46 && (e.which < 48 || e.which > 57))
        {
            e.which = 0;
            _ret = false;
        }
    }
    return (_ret);
}




function disableControl(E)
{
    var _ret = true;
    if (isIE)
    {
        window.event.keyCode = 0;
        _ret = false;
    }
    if (isNS)
    {
        e.which = 0;
        _ret = false;
    }
    return (_ret);
}
var lang;
function validateNoArabicInput(input)
{
    var bolReturn = true;
    if (input != "")
    {
        var cInput = new Array();
        for (var i = 0; i < input.length; i++)
        {
            cInput[i] = input.charCodeAt(i);
            if (parseInt(cInput[i]) >= 1571 && parseInt(cInput[i]) <= 1610)
                bolReturn = false;
        }

    }
    return bolReturn;
}
function validateNoSpace(input)
{
    var bolReturn = true;
    if (input != "")
    {
        var cInput = new Array();
        for (var i = 0; i < input.length; i++)
        {
            cInput[i] = input.charCodeAt(i);
            if (parseInt(cInput[i]) == 32)
                bolReturn = false;
        }
    }
    return bolReturn;
}

function validateHumanNames(input)
{
    var bolReturn = true;
    if (input != "")
    {
        var cInput = new Array();
        for (var i = 0; i < input.length; i++)
        {
            cInput[i] = input.charCodeAt(i);
            if ((parseInt(cInput[i]) >= 33 && parseInt(cInput[i]) <= 47) || (parseInt(cInput[i]) >= 58 && parseInt(cInput[i]) <= 64) || (parseInt(cInput[i]) >= 91 && parseInt(cInput[i]) <= 96) || (parseInt(cInput[i]) >= 123 && parseInt(cInput[i]) <= 126))
                bolReturn = false;
        }
    }
    return bolReturn;
}

function validateNoEnglishInput(input)
{
    var bolReturn = true;
    if (input != "")
    {
        var cInput = new Array();
        for (var i = 0; i < input.length; i++)
        {
            cInput[i] = input.charCodeAt(i);
            if ((parseInt(cInput[i]) >= 65 && parseInt(cInput[i]) <= 90) || (parseInt(cInput[i]) >= 97 && parseInt(cInput[i]) <= 122))
                bolReturn = false;
        }
    }
    return bolReturn;
}

function validateOnlyNumbersInput(input)
{
    var bolReturn = false;
    if (input != "")
    {
        var cInput = new Array();
        for (var i = 0; i < input.length; i++)
        {
            cInput[i] = input.charCodeAt(i);
            if ((parseInt(cInput[i]) >= 48 && parseInt(cInput[i]) <= 57))
                bolReturn = true;

            else
            {
                bolReturn = false;
                break;
            }
        }
    }
    else
        bolReturn = true;

    return bolReturn;
}


function validateDecimalInput(input)
{
    var bolReturn = false;
    if (input != "")
    {
        var firstIndex = input.indexOf('.');
        var lastIndex = input.lastIndexOf('.');
        if (firstIndex == lastIndex)
        {
            var cInput = new Array();
            for (var i = 0; i < input.length; i++)
            {
                cInput[i] = input.charCodeAt(i);
                if ((parseInt(cInput[i]) >= 48 && parseInt(cInput[i]) <= 57) || parseInt(cInput[i]) == 46)
                    bolReturn = true;
                else
                {
                    bolReturn = false;
                    break;
                }
            }
        }
    }
    else
        bolReturn = true;

    return bolReturn;
}

function validateTwoFieldsInput(smaller, bigger)
{
    if (smaller != "" && bigger != "")
    {
        if (parseFloat(smaller) >= parseFloat(bigger))
        {
            return false;
        }
        else
            return true;
    }
    else
        return true;
}

function validateSFID(input, label)
{
    var bolReturn = true;
    if (input != "")
    {
        var re = new RegExp("^[a-zA-Z0-9]{1}[a-zA-Z0-9_.]*$");
        if (!(input.match(re)))
            bolReturn = false;
    }
    return bolReturn;
}

function checkEmail(emailStr)
{
    if (emailStr.length == 0)
    {
        return true;
    }
    var emailPat = /^(.+)@(.+)$/;
    var specialChars = "\\(\\)<>@,;:\\\\\\\"\\.\\[\\]";
    var validChars = "\[^\\s" + specialChars + "\]";
    var quotedUser = "(\"[^\"]*\")";
    var ipDomainPat = /^(\d{1,3})[.](\d{1,3})[.](\d{1,3})[.](\d{1,3})$/;
    var atom = validChars + '+';
    var word = "(" + atom + "|" + quotedUser + ")";
    var userPat = new RegExp("^" + word + "(\\." + word + ")*$");
    var domainPat = new RegExp("^" + atom + "(\\." + atom + ")*$");
    var matchArray = emailStr.match(emailPat);
    if (matchArray == null)
    {
        return false;
    }
    var user = matchArray[1];
    var domain = matchArray[2];
    if (user.match(userPat) == null)
    {
        return false;
    }
    var IPArray = domain.match(ipDomainPat);
    if (IPArray != null)
    {
        for (var i = 1; i <= 4; i++)
        {
            if (IPArray[i] > 255)
            {
                return false;
            }
        }
        return true;
    }
    var domainArray = domain.match(domainPat);
    if (domainArray == null)
    {
        return false;
    }
    var atomPat = new RegExp(atom, "g");
    var domArr = domain.match(atomPat);
    var len = domArr.length;
    if ((domArr[domArr.length - 1].length < 2) || (domArr[domArr.length - 1].length > 3))
    {
        return false;
    }
    if (len < 2)
    {
        return false;
    }
    return true;
}


function validateEmail(input)
{
    var bolReturn = true;
    if (input != "")
    {
        var cInput = new Array();
        for (var i = 0; i < input.length; i++)
        {
            cInput[i] = input.charCodeAt(i);
            if (parseInt(cInput[i]) >= 1571 && parseInt(cInput[i]) <= 1610)
                bolReturn = false;
        }
        if (bolReturn)
            bolReturn = checkEmail(input);
    }
    return bolReturn;
}

function validateSpecialInput(input)
{
    var bolReturn = true;
    if (input != "")
    {
        var cInput = new Array();
        for (var i = 0; i < input.length; i++)
        {
            cInput[i] = input.charCodeAt(i);
            if ((parseInt(cInput[i]) >= 33 && parseInt(cInput[i]) <= 47 && parseInt(cInput[i]) != 45 && parseInt(cInput[i]) != 46) || (parseInt(cInput[i]) >= 58 && parseInt(cInput[i]) <= 64) || (parseInt(cInput[i]) >= 91 && parseInt(cInput[i]) <= 96 && parseInt(cInput[i]) != 95) || (parseInt(cInput[i]) >= 123 && parseInt(cInput[i]) <= 126))
                bolReturn = false;
        }
    }
    return bolReturn;
}

function validateMobile(input)
{
    var bolReturn = true;
    if (input != "")
    {
        var re = new RegExp("^[0-9]{10}$");
        if (!(input.match(re)))
            bolReturn = false;
    }
    return bolReturn;
}

function validatePhone(input)
{
    var bolReturn = true;
    if (input != "")
    {
        var re = new RegExp("^[0-9()-/]+$");
        if (!(input.match(re)))
            bolReturn = false;
    }
    return bolReturn;
}

function isPositiveNumbers(str) {
    return /^\+?(0|[1-9]\d*)$/.test(str);
}
