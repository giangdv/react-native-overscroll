import React, { Component } from 'react';
import { DeviceEventEmitter, requireNativeComponent } from 'react-native';

var OverScrollNative = requireNativeComponent('OverScroll', OverScroll);

export default class OverScroll extends Component {
  static defaultProps = {
    onScroll: ()=> {}
  }

  onScroll = (event)=> {
    this.props.onScroll(event);
  }

  componentDidMount(){
    DeviceEventEmitter.removeAllListeners();
    DeviceEventEmitter.addListener('onScroll', this.onScroll);
  }
  
  componentWillUnmount(){
    DeviceEventEmitter.removeAllListeners();
  }

  render () {
    return <OverScrollNative {...this.props} />
  }
}
