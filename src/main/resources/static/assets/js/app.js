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
// for now this script does not take into consideration
// adding step when there are none
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

    // TODO: add Nan check here

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


/**
 * changes select item element inside first div
 * of ingredientRowDiv:
 * - changes id,name
 * - removes "selected" attribute from <option>-s inside select
 * @param ingredientRowDiv - link to ingredientRowDiv with everything
 * related to one ingredient
 * @param newIngredientId - id in the array of recipe.ingredients
 * of the new ingredient to be added
 */
function changeSelectItemElement(
    ingredientRowDiv,
    newIngredientId) {

    var selectItemElement =
        ingredientRowDiv
        .children('div').eq(0)
        .children('p')
        .children();

    // change select id
    selectItemElement.attr(
            'id',
            'ingredients' + newIngredientId + '.item.id'
    );

    // change select name
    selectItemElement.attr(
            'name',
            'ingredients[' + newIngredientId + '].item.id'
    );

    // remove attribute selected from option
    selectItemElement
        .children() // option
        .removeAttr('selected');

}

/**
 * changes <input> with ingredientPropertyName
 * element inside of ingredientRowDiv, inside of
 * div with indexOfDivInIngredientRow:
 * <div> - ingredientRowDiv
 *    <div></div> - div #1
 *    ...
 *    <div> - div #indexOfDivInIngredientRow
 *       <p>
 *          <input> - input with ingredientPropertyName
 *       </p>
 *    </div>
 * <div>
 * @param ingredientRowDiv
 * @param newIdOfIngredient
 * @param indexOfDivInIngredientRow
 * @param ingredientPropertyName
 */
function changeIngredientInputElement(
    ingredientRowDiv,
    newIdOfIngredient,
    indexOfDivInIngredientRow,
    ingredientPropertyName) {

    var inputElementToChange =
        ingredientRowDiv
            .children('div').eq(indexOfDivInIngredientRow)
            .children('p')
            .children();

    // change ingredient.property 'id'
    // id="ingredient'newIdOfIngredient'.ingredientPropertyName"
    inputElementToChange.attr(
        'id',
        'ingredients' + newIdOfIngredient + '.' + ingredientPropertyName
    );
    // change ingredient.property 'name'
    // name="ingredient['newIdOfIngredient'].ingredientPropertyName"
    inputElementToChange.attr(
        'name',
        'ingredients[' + newIdOfIngredient + '].' + ingredientPropertyName
    );

    // remove values, so that inputs are blank
    inputElementToChange.removeAttr('value');

}


// function adding new ingredient
$("#add-another-ingredient-button").click(function () {
    // can add prevent default so that
    // no need to write button type=button
    // in html5
    event.preventDefault();

    // define <div> with ingredient that we will be appending to
    var divWithLastIngredient = $(".ingredient-row").last();

    // get id as String of the last step to generate new one
    // it is kind of wacky .. but well I'm JS newbie, so
    var idAsString =
        divWithLastIngredient
        .children('div').first()
        .children('p')
        .children()
        .attr('id')
        .split('.')[0]
        .split('ingredients')[1];

    // if id is not a number we set id to zero
    // otherwise we parse it
    var id = 0;
    if (!isNaN(idAsString)) {
        id = parseInt(idAsString);
    }

    // set new id
    var newIngredientId = id + 1;

    // clone div and add new one
    var cloneOfTheLastDivWithIngredientRow =
        divWithLastIngredient.clone();

    // add clone to the end
    divWithLastIngredient.after(
        cloneOfTheLastDivWithIngredientRow
    );

    // change ingredient.item select
    changeSelectItemElement(
        cloneOfTheLastDivWithIngredientRow,
        newIngredientId
    );

    // change ingredient.quantity
    changeIngredientInputElement(
        cloneOfTheLastDivWithIngredientRow,
        newIngredientId,
        1,
        'quantity'
    );

    // change ingredient.condition
    changeIngredientInputElement(
        cloneOfTheLastDivWithIngredientRow,
        newIngredientId,
        2,
        'condition'
    );

});
