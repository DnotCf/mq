import Modal from './confirm';

let modalInstance;

function getModalInstance (render, options) {
    render = render || undefined
    options = options || {}
    modalInstance = modalInstance || Modal.newInstance({
        closable: false,
        maskClosable: false,
        footerHide: true,
        className: 'geo-modal'+' confirm-modal ' + options.type,
        type:options.type,
        showCancel: options.showCancel,
        showIcon: options.showIcon,
        render: render

    });

    return modalInstance;
}

function confirm (options) {
    const render = ('render' in options) ? options.render : undefined;
    let instance  = getModalInstance(render,options);

    options.onRemove = function () {
        modalInstance = null;
    };

    instance.show(options);
}

Modal.info = function (props) {
    props = props || {}
    props.icon = 'info';
    props.showCancel = props.showCancel || false;
    props.type = 'info';
    props.okText = props.okText || '确定';
    props.showIcon = props.showIcon || false;
    return confirm(props);
};

Modal.success = function (props) {
    props = props || {}
    props.icon = 'success';
    props.showCancel = props.showCancel || false;
    props.type = 'success';
    props.okText = props.okText || '确定';
    props.showIcon = props.showIcon || false;
    return confirm(props);
};

Modal.warning = function (props) {
    props = props || {}
    props.icon = 'warning';
    props.showCancel = props.showCancel || false;
    props.type = 'warning';
    props.okText = props.okText || '确定';
    props.showIcon = props.showIcon || false;
    return confirm(props);
};

Modal.error = function (props) {
    props = props || {}
    props.icon = 'error';
    props.showCancel = props.showCancel || false;
    props.type = 'error';
    props.okText = props.okText || '确定';
    props.showIcon = props.showIcon || false;
    return confirm(props);
};

Modal.confirm = function (props) {
    props = props || {}
    props.icon = 'confirm';
    props.showCancel = props.showCancel || false;
    props.type = 'confirm';
    props.okText = props.okText || '确定';
    props.showIcon = props.showIcon || false;
    return confirm(props);
};

Modal.remove = function () {
    if (!modalInstance) {   // at loading status, remove after Cancel
        return false;
    }

    const instance = getModalInstance();

    instance.remove();
};

export default Modal;
