import Vue from 'vue'
import { Modal, Button } from 'view-design'

const prefixCls = 'ivu-modal-confirm';

Modal.newInstance = properties => {
    const _props = properties || {};
    const Instance = new Vue({
        /* mixins: [ Locale ], */
        data: Object.assign({}, _props, {
            visible: false,
            width: 416,
            title: '',
            body: '',
            iconType: '',
            iconName: '',
            theme: '',
            okText: undefined,
            cancelText: undefined,
            showCancel: false,
            showIcon: _props.showIcon,
            loading: false,
            buttonLoading: false,
            scrollable: false,
            closable: true,
            closing: false // 关闭有动画，期间使用此属性避免重复点击
        }),
        render (h) {
            let footerVNodes = [];
            try{
              footerVNodes.push(h(Button, {
                  props: {
                      type: 'primary',
                      size: 'large',
                      loading: this.buttonLoading
                  },
                  on: {
                      click: this.ok
                  }
              }, this.localeOkText));
            }catch(e){
              console.log(e);
              //TODO handle the exception
            }
            if (this.showCancel) {
                 footerVNodes.push(h(Button, {
                     props: {
                         type: 'text',
                         size: 'large'
                     },
                     on: {
                         click: this.cancel
                     }
                 }, this.localeCancelText));
             }
            // render content
            let body_render;
            if (this.render) {
                body_render = h('div', {
                    attrs: {
                        class: `${prefixCls}-body ${prefixCls}-body-render confirm-modal-body`
                    }
                }, [this.render(h)]);
            } else {
                body_render = h('div', {
                    attrs: {
                        class: `${prefixCls}-body confirm-modal-body`
                    }
                }, [
                    h('div', {
                        domProps: {
                            innerHTML: this.body
                        }
                    })
                ]);
            }

            // when render with no title, hide head
            let head_render;
            if (this.title && this.showIcon) {
                head_render = h('div', {
                    attrs: {
                        class: `${prefixCls}-head confirm-modal-header`
                    }
                }, [
                    h('div', {
                        class: this.iconTypeCls
                    }, [
                        h('i', {
                            class: this.iconNameCls
                        })
                    ]),
                    h('div', {
                        attrs: {
                            class: `${prefixCls}-head-title`
                        },
                        domProps: {
                            innerHTML: this.title
                        }
                    })
                ]);
            }else {
              head_render = h('div', {
                  attrs: {
                      class: `${prefixCls}-head confirm-modal-header`
                  }
              }, [
                  h('div', {
                      attrs: {
                          class: `${prefixCls}-head-title`
                      },
                      domProps: {
                          innerHTML: this.title
                      }
                  })
              ]);
            }

            return h(Modal, {
                props: Object.assign({}, _props, {
                    width: this.width,
                    scrollable: this.scrollable,
                    closable: this.closable
                }),
                domProps: {
                    value: this.visible
                },
                on: {
                    input: (status) => {
                        this.visible = status;
                    },
                    'on-cancel': this.cancel
                }
            }, [
                h('div', {
                  class: this.themeTypeCls
                }, [
                    head_render,
                    body_render,
                    h('div', {
                        attrs: {
                            class: `${prefixCls}-footer confirm-modal-footer`
                        }
                    }, footerVNodes)
                ])
            ]);
        },
        computed: {
            themeTypeCls() {
              return [
                `${prefixCls} ${prefixCls}-${this.theme}`,
              ];
            },
            iconTypeCls () {
                return [
                    `${prefixCls}-head-icon`,
                    `${prefixCls}-head-icon-${this.iconType}`
                ];
            },
            iconNameCls () {
                return [
                    'ivu-icon',
                    `ivu-icon-${this.iconName}`
                ];
            },
            localeOkText () {
                if (this.okText) {
                    return this.okText;
                } else {
                    return '确定';
                }
            },
            localeCancelText () {
                if (this.cancelText) {
                    return this.cancelText;
                } else {
                    return '取消';
                }
            }
        },
        methods: {
            cancel () {
                if (this.closing) return;
                this.$children[0].visible = false;
                this.buttonLoading = false;
                this.onCancel();
                this.remove();
            },
            ok () {
                if (this.closing) return;
                if (this.loading) {
                    this.buttonLoading = true;
                } else {
                    this.$children[0].visible = false;
                    this.remove();
                }
                this.onOk();
            },
            remove () {
                this.closing = true;
                setTimeout(() => {
                    this.closing = false;
                    this.destroy();
                }, 300);
            },
            destroy () {
                this.$destroy();
                document.body.removeChild(this.$el);
                this.onRemove();
            },
            onOk () {},
            onCancel () {},
            onRemove () {}
        }
    });
    const component = Instance.$mount();
    document.body.appendChild(component.$el);
    const modal = Instance.$children[0];

    return {
        show (props) {
            modal.$parent.showCancel = props.showCancel;
            modal.$parent.iconType = props.icon;
            switch (props.icon) {
                case 'info':
                    modal.$parent.iconName = 'ios-information-circle';
                    break;
                case 'success':
                    modal.$parent.iconName = 'ios-checkmark-circle';
                    break;
                case 'warning':
                    modal.$parent.iconName = 'ios-alert';
                    break;
                case 'error':
                    modal.$parent.iconName = 'ios-close-circle';
                    break;
                case 'confirm':
                    modal.$parent.iconName = 'ios-help-circle';
                    modal.$parent.theme = (props.theme || '');
                    break;
            }

            if ('width' in props) {
                modal.$parent.width = props.width;
            }

            if ('closable' in props) {
                modal.$parent.closable = props.closable;
            }

            if ('title' in props) {
                modal.$parent.title = props.title;
            }

            if ('content' in props) {
                modal.$parent.body = props.content;
            }

            if ('okText' in props) {
                modal.$parent.okText = props.okText;
            }

            if ('cancelText' in props) {
                modal.$parent.cancelText = props.cancelText;
            }

            if ('onCancel' in props) {
                modal.$parent.onCancel = props.onCancel;
            }

            if ('onOk' in props) {
                modal.$parent.onOk = props.onOk;
            }

            // async for ok
            if ('loading' in props) {
                modal.$parent.loading = props.loading;
            }

            if ('scrollable' in props) {
                modal.$parent.scrollable = props.scrollable;
            }

            // notice when component destroy
            modal.$parent.onRemove = props.onRemove;

            modal.visible = true;
        },
        remove () {
            modal.visible = false;
            modal.$parent.buttonLoading = false;
            modal.$parent.remove();
        },
        component: modal
    };
};

export default Modal;
