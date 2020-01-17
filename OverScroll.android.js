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
    DeviceEventEmitter.addListener('onScroll', this.onScroll);
  }
  
  componentWillUnmount(){
    DeviceEventEmitter.removeListener('onScroll', this.onScroll)
  }

  render () {
    return <OverScrollNative {...this.props} />
  }
}
