window.bookingUi = window.bookingUi || {};

window.bookingUi.formatSlot = (date, startTime, endTime) => {
    return `${date} ${startTime} - ${endTime}`;
};
