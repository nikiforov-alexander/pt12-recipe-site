// This script makes style of select the same as the option style selected
// uses jQuery. It is taken from here:
// http://jsfiddle.net/614c6cxz/8/
// original discussion on stackoverflow:
// http://stackoverflow.com/questions/15755770/change-text-color-of-selected-option-in-a-select-box
$("select").change(function(){
    $(this).removeClass($(this).attr('class'))
        .addClass($(":selected",this).attr('class'));
});

// function adding new step
$("#add-another-step-button").click(function () {
    // can add prevent default so that
    // no need to write button type=button
    // in html5
    event.preventDefault();

    // define <div> with step that we will be appending to
    var divWithLastStep = $(".step-row").last();

    // get id of the last step to generate new one
    // it is kind of whacky .. but well I'm JS newbie, so
    var id = parseInt(
        divWithLastStep
            .children('div')
            .children('p')
            .children('input')
            .attr('id')
            .split('.')[0]
            .split('steps')[1]
    );

    // set new id
    var newId = id + 1;
    var newStepInputId = 'steps' + newId + '.description';
    var newStepInputName = 'steps[' + newId + '].description';

    // create new div
    var newDiv =
        "<div class='step-row'>" +
            "<div class='prefix-20 grid-80'>" +
                "<p>" +
                    "<input id='" + newStepInputId + "' " +
                    "name='" + newStepInputName + "' " +
                    "placeholder='new step'>" +
                "</p>" +
            "</div>" +
        "</div>";

    // add newly created div after last one
    divWithLastStep.after(
        newDiv
    );

});