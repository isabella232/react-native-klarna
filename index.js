// @flow
import * as React from 'react';
import { requireNativeComponent } from 'react-native';
import type { ViewStyleProp } from 'react-native/Libraries/StyleSheet/StyleSheet';

type Props = {
  snippet: string,
  onComplete?: (nativeEvent: NativeEvent) => void,
  style?: ViewStyleProp,
};

export type NativeEvent = {
  data: {
    order_url?: string,
    uri?: string,
  },
  signalType: string,
  target: number,
  type: string,
};

export default class RNKlarna extends React.Component<Props> {
  static defaultProps = {
    snippet: '',
  };

  _onComplete = ({ nativeEvent }: { nativeEvent: NativeEvent }) => {
    const { onComplete } = this.props;
    if (onComplete) {
      onComplete(nativeEvent);
    }
  };

  render() {
    const { snippet, style } = this.props;
    return <Klarna style={style} snippet={snippet} onComplete={this._onComplete} />;
  }
}

const Klarna = requireNativeComponent('RNKlarna');
